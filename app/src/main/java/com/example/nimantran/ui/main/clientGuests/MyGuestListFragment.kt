package com.example.nimantran.ui.main.clientGuests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.adapters.MyGuestListAdapter
import com.example.nimantran.databinding.FragmentMyGuestListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyGuestListFragment : Fragment(){
    private var _binding: FragmentMyGuestListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val myGuestListViewModel: MyGuestViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_guest_list, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myGuestListViewModel.getGuests(db,auth.currentUser?.uid) // fetch data only
        myGuestListViewModel.guests.observe(viewLifecycleOwner) { guests ->
            if (guests.isNotEmpty()) {
                binding.recyclerViewMyGuestList.adapter =
                    MyGuestListAdapter(requireActivity()) {
                        myGuestListViewModel.selectGuest(it)
                        val dir =
                            MyGuestListFragmentDirections.actionMyGuestListFragmentToEditGuestFragment(
                                it.id
                            )
                    }
                (binding.recyclerViewMyGuestList.adapter as MyGuestListAdapter).submitList(
                    guests
                )
            } else {
                binding.recyclerViewMyGuestList.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutMyGuestList.isRefreshing) {
                binding.swipeRefreshLayoutMyGuestList.isRefreshing = false
            }
        }
        binding.swipeRefreshLayoutMyGuestList.setOnRefreshListener {
            myGuestListViewModel.getGuests(db, auth.currentUser?.uid)
        }
        binding.cardViewAddGuest.setOnClickListener {
            val dir = MyGuestListFragmentDirections.actionMyGuestListFragmentToAddMyGuestFragment()
            findNavController().navigate(dir)
        }
        binding.searchViewMyGuestList.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchGuests(newText)
                }
                return true
            }
        })
    }

    private fun searchGuests(newText: String) {
        //search guests
        val searchQuery = newText.toLowerCase()
        val searchResult = myGuestListViewModel.guests.value?.filter {
            it.name.toLowerCase().contains(searchQuery) ||
                    it.phone.toLowerCase().contains(searchQuery) ||
                    it.name.toLowerCase().contains(searchQuery)
        }
        if (searchResult != null) {
            binding.recyclerViewMyGuestList.adapter =
                MyGuestListAdapter(requireContext()){
                    myGuestListViewModel.selectGuest(it)
                }
            (binding.recyclerViewMyGuestList.adapter as MyGuestListAdapter).submitList(searchResult)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val COLL_MY_GUESTS = "myGuests"
    }
}