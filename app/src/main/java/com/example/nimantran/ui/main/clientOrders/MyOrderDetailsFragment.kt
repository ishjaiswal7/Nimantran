package com.example.nimantran.ui.main.clientOrders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.nimantran.databinding.FragmentMyOrderDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyOrderDetailsFragment : Fragment() {
    private var _binding: FragmentMyOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val myOrdersViewModel: MyOrdersViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    companion object {
        fun newInstance() = MyOrderDetailsFragment()
    }
}