package com.example.nimantran.ui.main.clientOrders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.adapters.MyOrdersAdapter
import com.example.nimantran.databinding.FragmentMyOrdersBinding
import com.google.firebase.firestore.FirebaseFirestore

class MyOrdersFragment : Fragment() {
    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val myOrdersViewModel: MyOrdersViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myOrdersViewModel.getMyOrders(db)
        myOrdersViewModel.myorders.observe(viewLifecycleOwner) { orders ->
            if (orders.isNotEmpty()) {
                binding.recyclerViewMyOrders.adapter = MyOrdersAdapter(requireActivity()) {
                    myOrdersViewModel.selectOrder(it)
                    findNavController().navigate(R.id.action_myOrdersFragment_to_myOrderDetailsFragment)
                }
                (binding.recyclerViewMyOrders.adapter as MyOrdersAdapter).submitList(orders)
            } else {
                binding.recyclerViewMyOrders.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutMyOrders.isRefreshing) {
                binding.swipeRefreshLayoutMyOrders.isRefreshing = false
            }
        }
        // swipe to refresh
        binding.swipeRefreshLayoutMyOrders.setOnRefreshListener {
            myOrdersViewModel.getMyOrders(db)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val COLL_ORDERS = "myorders"
    }
}