package com.example.nimantran.ui.main.mycards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nimantran.R
import com.example.nimantran.adapters.user.InviteResponseListAdapter
import com.example.nimantran.databinding.FragmentInviteResponseListBinding
import com.example.nimantran.ui.main.addcard.TemplateCardViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InviteResponseListFragment : Fragment() {
    private var _binding: FragmentInviteResponseListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val myCardViewModel: MyCardsViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invite_response_list, container, false)
        binding.myCardViewModel = myCardViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get myCard from MyCardsFragment
        val selectedMyCard = myCardViewModel.selectedMyCard
        selectedMyCard.observe(viewLifecycleOwner) { myCard ->
        }

        // get inviteResponseList of MyCard
        myCardViewModel.getInviteOfMyCard()
        myCardViewModel.invite.observe(viewLifecycleOwner) { invite ->
            if (invite != null) {
                binding.recyclerViewInviteResponseList.layoutManager = LinearLayoutManager(context)
                binding.recyclerViewInviteResponseList.adapter = InviteResponseListAdapter(requireContext()){
                }
                (binding.recyclerViewInviteResponseList.adapter as InviteResponseListAdapter).submitList(selectedMyCard.value?.invite)
            }
        }
        binding.swipeRefreshLayoutInviteResponseList.setOnRefreshListener {
            binding.swipeRefreshLayoutInviteResponseList.isRefreshing = false
        }
        binding.searchViewMyCardResponses.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchMyCardResponses(newText)
                }
                return true
            }
        })
    }
    private fun searchMyCardResponses(newText: String) {
        val searchQuery = newText.toLowerCase()
        val searchResult = myCardViewModel.invite.value?.filter {
            it.response.toLowerCase().contains(searchQuery) ||
                    it.guestName.toLowerCase().contains(searchQuery) ||
                    it.phone.toLowerCase().contains(searchQuery)
        }
        if (searchResult != null) {
            binding.recyclerViewInviteResponseList.layoutManager = LinearLayoutManager(context)
            binding.recyclerViewInviteResponseList.adapter = InviteResponseListAdapter(requireContext()){
            }
            (binding.recyclerViewInviteResponseList.adapter as InviteResponseListAdapter).submitList(searchResult)
        }
    }
    companion object {
    }
}