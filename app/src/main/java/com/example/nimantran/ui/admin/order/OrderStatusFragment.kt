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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nimantran.R
import com.example.nimantran.adapters.OrderListAdapter
import com.example.nimantran.adapters.admin.GuestListAdapter
import com.example.nimantran.databinding.FragmentOrderStatusBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderStatusFragment : Fragment() {
    private var _binding: FragmentOrderStatusBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val orderListViewModel: OrderListViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
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

        binding.textViewUserName.text = orderListViewModel.client.value?.name.toString()
        binding.textViewUserPhone.text = orderListViewModel.client.value?.phone.toString()
        orderListViewModel.selectedOrder.observe(viewLifecycleOwner) { order ->
            Toast.makeText(requireContext(), "order loaded", Toast.LENGTH_SHORT).show()
        }

        orderListViewModel.getGuests(
            db,
            orderListViewModel.selectedOrder.value?.clientID.toString()
        ) // fetch data only
        orderListViewModel.guests.observe(viewLifecycleOwner) { guests ->
            if (guests.isNotEmpty()) {
                binding.recyclerViewOrderList.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerViewOrderList.adapter = OrderListAdapter(requireContext()) {
                }
                (binding.recyclerViewOrderList.adapter as GuestListAdapter).submitList(
                    orderListViewModel.selectedOrder.value?.guest
                )
            } else {
                binding.recyclerViewOrderList.visibility = View.GONE
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val COLL_GUESTS = "myGuests"
        const val COLL_ORDERS = "myOrders"
        const val COLL_CLIENTS = "clients"
    }

}