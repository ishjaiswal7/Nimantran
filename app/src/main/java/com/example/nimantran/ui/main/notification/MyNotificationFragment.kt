package com.example.nimantran.ui.main.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.adapters.MyNotificationAdapter
import com.example.nimantran.databinding.FragmentMyNotificationBinding
import com.example.nimantran.ui.main.notification.MyNotificationFragmentDirections
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyNotificationFragment : Fragment() {


    private var _binding: FragmentMyNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val myNotificationViewModel: MyNotificationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_notification, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.myNotificationViewModel = myNotificationViewModel

        myNotificationViewModel.getMyNotifications(db) // fetch data only
        myNotificationViewModel.myNotifications.observe(viewLifecycleOwner) { notifications ->
            if (notifications.isNotEmpty()) {
                binding.recyclerViewMyNotification.adapter =
                    MyNotificationAdapter(requireActivity()) {
                        myNotificationViewModel.selectMyNotification(it)
                        val dir =
                            MyNotificationFragmentDirections.actionMyNotificationFragmentToMyReadNotificationFragment()
                        findNavController().navigate(dir)
                    }

                (binding.recyclerViewMyNotification.adapter as MyNotificationAdapter).submitList(
                    notifications
                )
            } else {
                binding.recyclerViewMyNotification.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutMyNotification.isRefreshing) {
                binding.swipeRefreshLayoutMyNotification.isRefreshing = false
            }
        }
        binding.searchViewMyNotification.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchMyNotifications(newText)
                }
                return true
            }
        })
        // swipe to refresh
        binding.swipeRefreshLayoutMyNotification.setOnRefreshListener {
            myNotificationViewModel.getMyNotifications(db)
        }
    }

    private fun searchMyNotifications(newText: String) {
        val searchQuery = newText.toLowerCase()
        val searchResult = myNotificationViewModel.myNotifications.value?.filter {
            it.subject.toLowerCase().contains(searchQuery) ||
                    it.body.toLowerCase().contains(searchQuery)
        }
        if (searchResult != null) {
            binding.recyclerViewMyNotification.adapter =
                MyNotificationAdapter(requireActivity()) {
                    myNotificationViewModel.selectMyNotification(it)
                    val dir =
                        MyNotificationFragmentDirections.actionMyNotificationFragmentToMyReadNotificationFragment()
                    findNavController().navigate(dir)
                }
            (binding.recyclerViewMyNotification.adapter as MyNotificationAdapter).submitList(
                searchResult
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val COLL_MY_NOTIFICATIONS = "notifications"
    }
}