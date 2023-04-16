package com.example.nimantran.ui.admin.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.nimantran.R
import com.example.nimantran.adapters.MyGuestListAdapter
import com.example.nimantran.adapters.OrderListAdapter
import com.example.nimantran.adapters.admin.GuestListAdapter
import com.example.nimantran.databinding.FragmentOrderStatusBinding
import com.example.nimantran.models.user.Guest
import com.example.nimantran.ui.main.clientGuests.MyGuestListFragmentDirections
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



        orderListViewModel.getGuests(db, orderListViewModel.selectedOrder.value?.clientID.toString()) // fetch data only
        orderListViewModel.guests.observe(viewLifecycleOwner) { guests ->
            if (guests.isNotEmpty()) {
                binding.recyclerViewOrderList.adapter =
                    GuestListAdapter(requireActivity()) {
                        //orderListViewModel.selectGuest(it)
                        val dir =
                            MyGuestListFragmentDirections.actionMyGuestListFragmentToEditGuestFragment(
                                it.id
                            )
                        //                       findNavController().navigate(dir)
                    }


                (binding.recyclerViewOrderList.adapter as GuestListAdapter).submitList(
                    guests
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