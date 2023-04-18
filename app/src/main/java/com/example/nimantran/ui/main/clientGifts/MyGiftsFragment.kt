package com.example.nimantran.ui.main.clientGifts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.adapters.user.MyGiftAdapter
import com.example.nimantran.databinding.FragmentMyGiftsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyGiftsFragment : Fragment() {
    private var _binding: FragmentMyGiftsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val myGiftsViewModel: MyGiftsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyGiftsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myGiftsViewModel.getMyGifts(db) // fetch data only
        myGiftsViewModel.myGifts.observe(viewLifecycleOwner) { gifts ->
            if (gifts.isNotEmpty()) {
                binding.recyclerViewMyGifts.adapter = MyGiftAdapter(requireActivity()) {
                    myGiftsViewModel.selectMyGift(it)
                    findNavController().navigate(R.id.action_myGiftsFragment_to_myGiftDetailsFragment)
                }
                (binding.recyclerViewMyGifts.adapter as MyGiftAdapter).submitList(gifts)
            } else {
                binding.recyclerViewMyGifts.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutMyGifts.isRefreshing) {
                binding.swipeRefreshLayoutMyGifts.isRefreshing = false
            }
        }

        binding.swipeRefreshLayoutMyGifts.setOnRefreshListener {
            myGiftsViewModel.getMyGifts(db)
        }

        binding.imageViewMyOrders.setOnClickListener {
            findNavController().navigate(R.id.action_myGiftsFragment_to_myOrdersFragment)
        }
        binding.searchViewMyGifts.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchMyGifts(newText)
                }
                return true
            }
        })
    }

    private fun searchMyGifts(newText: String) {
        //search my gifts by item or description or price
        val searchQuery = newText.toLowerCase()
        val searchResult = myGiftsViewModel.myGifts.value?.filter {
            it.item.toLowerCase().contains(searchQuery) ||
                    it.description.toLowerCase().contains(searchQuery) ||
                    it.price.toString().contains(searchQuery)
        }
        if (searchResult != null) {
            binding.recyclerViewMyGifts.adapter = MyGiftAdapter(requireActivity()) {
                myGiftsViewModel.selectMyGift(it)
                findNavController().navigate(R.id.action_myGiftsFragment_to_myGiftDetailsFragment)
            }
            (binding.recyclerViewMyGifts.adapter as MyGiftAdapter).submitList(searchResult)
        }

    }

    companion object {
        const val COLL_MY_GIFTS = "gifts"
    }
}