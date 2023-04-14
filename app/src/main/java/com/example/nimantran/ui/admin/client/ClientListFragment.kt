package com.example.nimantran.ui.admin.client

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
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
    }

    companion object {
        const val COLL_CLIENTS = "clients"
    }
}