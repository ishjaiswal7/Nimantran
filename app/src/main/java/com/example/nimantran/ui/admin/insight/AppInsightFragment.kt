package com.example.nimantran.ui.admin.insight

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
import com.example.nimantran.R.layout.fragment_app_insight
import com.example.nimantran.databinding.FragmentAppInsightBinding
import com.example.nimantran.ui.admin.client.ClientListViewModel
import com.example.nimantran.ui.admin.gift.GiftViewModel
import com.example.nimantran.ui.admin.notification.NotificationListViewModel
import com.example.nimantran.ui.admin.order.OrderListViewModel
import com.example.nimantran.ui.main.addcard.TemplateCardViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppInsightFragment : Fragment() {

    private var _binding: FragmentAppInsightBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val appInsightViewModel: AppInsightViewModel by viewModels()
    private val clientListViewModel: ClientListViewModel by activityViewModels()
    private val giftListViewModel: GiftViewModel by activityViewModels()
    private val orderListViewModel: OrderListViewModel by activityViewModels()
    private val notificationListViewModel: NotificationListViewModel by activityViewModels()
    private val templateCardViewModel: TemplateCardViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, fragment_app_insight, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Total clients
        clientListViewModel.getClients(db)
        clientListViewModel.clients.observe(viewLifecycleOwner) { clients ->
            binding.textViewTotalClients.text = clients.size.toString()
        }

        //Total Gifts
        giftListViewModel.getGifts(db)
        giftListViewModel.gifts.observe(viewLifecycleOwner) { gifts ->
            binding.textViewTotalGifts.text = gifts.size.toString()
        }

        //Total Orders
        orderListViewModel.getOrders(db)
        orderListViewModel.orders.observe(viewLifecycleOwner) { orders ->
            binding.textViewTotalOrders.text = orders.size.toString()
        }

        //Total Notifications
        notificationListViewModel.getNotifications(db)
        notificationListViewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            binding.textViewTotalNotifications.text = notifications.size.toString()
        }

        //Total Templates
        templateCardViewModel.getTemplates(db)
        templateCardViewModel.templates.observe(viewLifecycleOwner) { templates ->
            binding.textViewTotalTemplates.text = templates.size.toString()
        }

        binding.imageViewBackFromInsights.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    companion object {
        const val COLL_CLIENTS = "clients"
    }
}