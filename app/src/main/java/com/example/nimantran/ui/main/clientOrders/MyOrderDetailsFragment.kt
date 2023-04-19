package com.example.nimantran.ui.main.clientOrders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nimantran.R
import com.example.nimantran.adapters.SelectedGuestForGiftAdapter
import com.example.nimantran.adapters.user.SelectedMyGuestForGiftSentAdapter
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
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_order_details, container, false)
        binding.viewModelMyOrders = myOrdersViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get order from MyOrdersFragment
        val selectedOrder = myOrdersViewModel.selectedOrder
        selectedOrder.observe(viewLifecycleOwner) { order ->
        }
        //get guest of Order
        myOrdersViewModel.getGuest() // fetch data only
        myOrdersViewModel.guests.observe(viewLifecycleOwner) { guests ->
            if(guests.isNotEmpty()){
                binding.recyclerViewSelectedMyGuestForGiftSent.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerViewSelectedMyGuestForGiftSent.adapter = SelectedMyGuestForGiftSentAdapter(requireContext()){

                }
                (binding.recyclerViewSelectedMyGuestForGiftSent.adapter as SelectedMyGuestForGiftSentAdapter).submitList(
                    selectedOrder.value?.guest
                )
            }else{
                binding.recyclerViewSelectedMyGuestForGiftSent.visibility = View.GONE
            }
        }
    }

    companion object {
        fun newInstance() = MyOrderDetailsFragment()
    }
}