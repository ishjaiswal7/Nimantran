package com.example.nimantran.ui.admin.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.nimantran.R
import com.example.nimantran.adapters.OrderListAdapter
import com.example.nimantran.databinding.FragmentOrderListBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderListFragment : Fragment() {

    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : FirebaseFirestore
    private val orderListViewModel: OrderListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_order_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderListViewModel.getOrders(db) // fetch data only
        orderListViewModel.orders.observe(viewLifecycleOwner) { orders ->
            if (orders.isNotEmpty()) {
                binding.recyclerViewOrderList.adapter =
                    OrderListAdapter(
                        requireActivity(),
                    ) {
                        orderListViewModel.selectOrder(it)
                    }

                (binding.recyclerViewOrderList.adapter as OrderListAdapter).submitList(orders)
            } else {
                binding.recyclerViewOrderList.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutOrderList.isRefreshing) {
                binding.swipeRefreshLayoutOrderList.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val COLL_ORDERS = "orders"
    }
}