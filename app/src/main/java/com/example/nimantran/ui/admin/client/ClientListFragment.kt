package com.example.nimantran.ui.admin.client

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.adapters.ClientListAdapter
import com.example.nimantran.databinding.FragmentClientListBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ClientListFragment : Fragment() {

    private var _binding: FragmentClientListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val userListViewModel: ClientListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_client_list, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userListViewModel.getClients(db) // fetch data only
        userListViewModel.clients.observe(viewLifecycleOwner) { clients ->
            if (clients.isNotEmpty()) {
                binding.recyclerViewUserList.adapter =
                    ClientListAdapter(requireActivity()) {
                        userListViewModel.selectClient(it)
                    }

                (binding.recyclerViewUserList.adapter as ClientListAdapter).submitList(clients)
            } else {
                binding.recyclerViewUserList.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutUserList.isRefreshing) {
                binding.swipeRefreshLayoutUserList.isRefreshing = false
            }
        }
        binding.imageViewBackFromClientList.setOnClickListener {
            //go back to admin dashboard
            findNavController().navigateUp()
            onDestroyView()
        }
        binding.searchViewUserList.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchUser(newText)
                }
                return true
            }
        })
    }

    private fun searchUser(newText: String) {
        //search user by id or name or phone or gender
        val searchQuery = newText.toLowerCase()
        val searchResult = userListViewModel.clients.value?.filter {
            it.id.toLowerCase().contains(searchQuery) ||
            it.name.toLowerCase().contains(searchQuery) ||
                    it.phone.toString().toLowerCase().contains(searchQuery) ||
                    it.gender.toLowerCase().contains(searchQuery)
        }
        if (searchResult != null) {
            binding.recyclerViewUserList.adapter =
                ClientListAdapter(requireActivity()) {
                    userListViewModel.selectClient(it)
                }
            (binding.recyclerViewUserList.adapter as ClientListAdapter).submitList(searchResult)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val COLL_CLIENTS = "clients"
    }
}