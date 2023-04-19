package com.example.nimantran.ui.main.clientOrders

import android.os.Bundle
import android.text.Selection
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.example.nimantran.adapters.MyOrdersAdapter
import com.example.nimantran.databinding.FragmentMyOrdersBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

class MyOrdersFragment : Fragment() {
    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val myOrdersViewModel: MyOrdersViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentMyOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myOrdersViewModel.getMyOrders(db, auth.currentUser?.uid)// get my orders
        myOrdersViewModel.myOrders.observe(viewLifecycleOwner) { orders ->
            if (orders.isNotEmpty()) {
                binding.recyclerViewMyOrders.adapter =
                    MyOrdersAdapter(requireActivity()) {
                    myOrdersViewModel.selectMyOrder(it)
                        // nav to order details
                    findNavController().navigate(R.id.action_myOrdersFragment_to_myOrderDetailsFragment)
                }
                (binding.recyclerViewMyOrders.adapter as MyOrdersAdapter).submitList(orders)
            } else {
                binding.recyclerViewMyOrders.visibility = View.GONE
                binding.textViewNoData.visibility = View.VISIBLE
                binding.imageViewNoData.visibility = View.VISIBLE
                binding.searchViewMyOrder.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutMyOrders.isRefreshing) {
                binding.swipeRefreshLayoutMyOrders.isRefreshing = false
            }
        }
        // swipe to refresh
        binding.swipeRefreshLayoutMyOrders.setOnRefreshListener {
            myOrdersViewModel.getMyOrders(db, auth.currentUser?.uid)
        }

        binding.imageViewBackFromMyOrdersList.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val COLL_ORDERS = "myOrders"
    }
}