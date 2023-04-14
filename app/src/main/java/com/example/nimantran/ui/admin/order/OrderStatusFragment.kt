package com.example.nimantran.ui.admin.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentOrderStatusBinding
import com.google.firebase.firestore.FirebaseFirestore

class OrderStatusFragment : Fragment() {
    private var _binding: FragmentOrderStatusBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : FirebaseFirestore
    private val orderListViewModel: OrderListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_status, container, false)
        return binding.root
    }

}