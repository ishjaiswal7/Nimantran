package com.example.nimantran.ui.admin.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentUpdateOrderStatusBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore

class UpdateOrderStatusFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentUpdateOrderStatusBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : FirebaseFirestore
    private val orderListViewModel: OrderListViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_order_status, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        binding.order = orderListViewModel
        //update order status on radio button click
        binding.radioGroupOrderStatus.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radioButtonOrderPlaced -> {
                    orderListViewModel.updateOrderStatus(db, "Order Placed")
                    Toast.makeText(context, "Order Placed", Toast.LENGTH_SHORT).show()
                }
                R.id.radioButtonOrderConfirmed -> {
                    orderListViewModel.updateOrderStatus(db, "Order Confirmed")
                    Toast.makeText(context, "Order Confirmed", Toast.LENGTH_SHORT).show()
                }
                R.id.radioButtonOrderDispatched -> {
                    orderListViewModel.updateOrderStatus(db, "Order Dispatched")
                    Toast.makeText(context, "Order Dispatched", Toast.LENGTH_SHORT).show()
                }
                R.id.radioButtonOrderDelivered -> {
                    orderListViewModel.updateOrderStatus(db, "Order Delivered")
                    Toast.makeText(context, "Order Delivered", Toast.LENGTH_SHORT).show()
                }
                R.id.radioButtonOrderAddressNotFound -> {
                    orderListViewModel.updateOrderStatus(db, "Order Address Not Found")
                    Toast.makeText(context, "Order Address Not Found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}