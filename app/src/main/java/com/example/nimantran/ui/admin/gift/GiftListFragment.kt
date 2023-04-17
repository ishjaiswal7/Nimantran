package com.example.nimantran.ui.admin.gift

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.adapters.GiftAdapter
import com.example.nimantran.databinding.FragmentGiftListBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GiftListFragment : Fragment() {
    private var _binding: FragmentGiftListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val giftViewModel: GiftViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gift_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        giftViewModel.getGifts(db) // fetch data only
        giftViewModel.gifts.observe(viewLifecycleOwner) { gifts ->
            if (gifts.isNotEmpty()) {
                binding.recyclerViewGiftList.adapter =
                    GiftAdapter(requireActivity(),) {
                        giftViewModel.selectGift(it)
                        val dir =
                            GiftListFragmentDirections.actionGiftListFragmentToEditGiftFragment(it.item)
                        findNavController().navigate(dir)
                    }
                (binding.recyclerViewGiftList.adapter as GiftAdapter).submitList(
                    gifts
                )
            } else {
                binding.recyclerViewGiftList.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutGiftList.isRefreshing) {
                binding.swipeRefreshLayoutGiftList.isRefreshing = false
            }
        }
        // swipe to refresh
        binding.swipeRefreshLayoutGiftList.setOnRefreshListener {
            giftViewModel.getGifts(db)
        }

        binding.fabAddGift.setOnClickListener {
            findNavController().navigate(R.id.action_giftListFragment_to_addGiftFragment) }

        binding.imageViewBackFromGiftList.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.searchViewGiftList.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchGifts(newText)
                }
                return true
            }
        })
    }

    private fun searchGifts(newText: String) {
        // search in gifts by item or description or price or quantity
        val searchQuery = newText.toLowerCase()
        val searchResult = giftViewModel.gifts.value?.filter {
            it.item.toLowerCase().contains(searchQuery) ||
                    it.description.toLowerCase().contains(searchQuery) ||
                    it.price.toString().contains(searchQuery) ||
                    it.quantity.toString().contains(searchQuery)
        }
        if (searchResult != null) {
            binding.recyclerViewGiftList.adapter = GiftAdapter(requireActivity()) {
                giftViewModel.selectGift(it)
                val dir =
                    GiftListFragmentDirections.actionGiftListFragmentToEditGiftFragment(it.item)
                findNavController().navigate(dir)
            }
            (binding.recyclerViewGiftList.adapter as GiftAdapter).submitList(searchResult)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val COLL_GIFTS = "gifts"
    }
}