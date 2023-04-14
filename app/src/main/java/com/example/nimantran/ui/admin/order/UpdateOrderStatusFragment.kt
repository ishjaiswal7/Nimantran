package com.example.nimantran.ui.admin.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentUpdateOrderStatusBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UpdateOrderStatusFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentUpdateOrderStatusBinding? = null
    private val binding get() = _binding!!
    private val orderListViewModel: OrderListViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_order_status, container, false)
        return binding.root
    }

}