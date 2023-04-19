package com.example.nimantran.ui.admin.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                    orderListViewModel.updateOrderStatus(db, "@string/order_placed")
                }
                R.id.radioButtonOrderConfirmed -> {
                    orderListViewModel.updateOrderStatus(db, "@string/order_confirmed")
                }
                R.id.radioButtonOrderDispatched -> {
                    orderListViewModel.updateOrderStatus(db, "@string/order_dispatched")
                }
                R.id.radioButtonOrderDelivered -> {
                    orderListViewModel.updateOrderStatus(db, "@string/order_delivered")
                }
                R.id.radioButtonOrderAddressNotFound -> {
                    orderListViewModel.updateOrderStatus(db, "@string/order_address_not_found")
                }
            }
            //dismiss bottom sheet
            dismiss()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}