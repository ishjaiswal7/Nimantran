package com.example.nimantran.ui.admin.order

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
import com.example.nimantran.adapters.OrderListAdapter
import com.example.nimantran.databinding.FragmentOrderListBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderListFragment : Fragment() {

    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : FirebaseFirestore
    private val orderListViewModel: OrderListViewModel by activityViewModels()

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

                        findNavController().navigate(R.id.action_orderListFragment_to_orderStatusFragment)
                    }

                (binding.recyclerViewOrderList.adapter as OrderListAdapter).submitList(orders)
            } else {
                binding.recyclerViewOrderList.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutOrderList.isRefreshing) {
                binding.swipeRefreshLayoutOrderList.isRefreshing = false
            }
        }

        binding.imageViewBackFromOrderList.setOnClickListener {
            findNavController().navigateUp()
        }
        //search order
        binding.searchViewOrderList.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchOrder(newText)
                }
                return true
            }
        })
    }

    private fun searchOrder(newText: String) {
        //search order by order id or order date or order status or sent to or total amount or gift quantity or gift item
        val searchQuery = newText.toLowerCase()
        val searchResult = orderListViewModel.orders.value?.filter {
            it.id.toLowerCase().contains(searchQuery) ||
                    it.orderDate.toString().toLowerCase().contains(searchQuery) ||
                    it.orderStatus.toLowerCase().contains(searchQuery) ||
                    it.sentTo.toLowerCase().contains(searchQuery) ||
                    it.totalAmount.toString().toLowerCase().contains(searchQuery) ||
                    it.giftQty.toLowerCase().contains(searchQuery) ||
                    it.gift.item.toLowerCase().contains(searchQuery)
        }
        if (searchResult != null) {
            binding.recyclerViewOrderList.adapter =
                OrderListAdapter(
                    requireActivity(),
                ) {
                    orderListViewModel.selectOrder(it)
                    findNavController().navigate(R.id.action_orderListFragment_to_orderStatusFragment)
                }
            (binding.recyclerViewOrderList.adapter as OrderListAdapter).submitList(searchResult)
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