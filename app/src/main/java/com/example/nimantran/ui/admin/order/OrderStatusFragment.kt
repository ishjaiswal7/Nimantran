package com.example.nimantran.ui.admin.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nimantran.R
import com.example.nimantran.adapters.OrderListAdapter
import com.example.nimantran.adapters.admin.GuestListAdapter
import com.example.nimantran.databinding.FragmentOrderStatusBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderStatusFragment : Fragment() {
    private var _binding: FragmentOrderStatusBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private val orderListViewModel: OrderListViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order_status, container, false)
        binding.order = orderListViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get order from OrderListFragment
        val selectedOrder = orderListViewModel.selectedOrder
        selectedOrder.observe(viewLifecycleOwner) { order ->
        }

        // get client of Order
        orderListViewModel.getClient(db) // fetch data only
        binding.textViewUserName.text = orderListViewModel.selectedOrderClientName
        binding.textViewUserPhone.text = orderListViewModel.selectedOrderClientPhone

        // get guests of Order
        orderListViewModel.getGuests() // fetch data only
        orderListViewModel.guests.observe(viewLifecycleOwner) { guests ->
            if (guests.isNotEmpty()) {
                binding.recyclerViewOrderGuestList.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerViewOrderGuestList.adapter = GuestListAdapter(requireContext()) {
                }
                (binding.recyclerViewOrderGuestList.adapter as GuestListAdapter).submitList(
                    selectedOrder.value?.guest
                )
                //Toast number of guests in order
                Toast.makeText(
                    requireContext(),
                    "Number of guests in order: ${selectedOrder.value?.guest?.size}",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                binding.recyclerViewOrderGuestList.visibility = View.GONE
            }
        }

        binding.imageViewBackFromOrderStatus.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.buttonUpdateOrderStatus.setOnClickListener {
            findNavController().navigate(R.id.action_orderStatusFragment_to_updateOrderStatusFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val COLL_ORDERS = "myOrders"
        const val COLL_CLIENTS = "clients"
    }

}