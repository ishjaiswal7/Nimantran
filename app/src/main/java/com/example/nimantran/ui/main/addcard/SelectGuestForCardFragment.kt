package com.example.nimantran.ui.main.addcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.adapters.SelectGuestAdapter
import com.example.nimantran.databinding.FragmentSelectGuestForCardBinding
import com.example.nimantran.ui.main.clientGuests.MyGuestViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SelectGuestForCardFragment : Fragment() {
    private var _binding: FragmentSelectGuestForCardBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val cardViewModel: TemplateCardViewModel by activityViewModels()
    private val guestViewModel: MyGuestViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_select_guest_for_card,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        guestViewModel.guests.observe(viewLifecycleOwner) { guests ->
            if (guests.isNotEmpty()) {
                val selectGuestAdapter = SelectGuestAdapter { guest, bool ->
                    guestViewModel.updateInvitedGuest(
                        guest,
                        bool
                    )
                }
                binding.recyclerViewSelectMyCardGuest.adapter = selectGuestAdapter
                selectGuestAdapter.submitList(guests)
            } else {
                binding.recyclerViewSelectMyCardGuest.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutMyCardGuest.isRefreshing) {
                binding.swipeRefreshLayoutMyCardGuest.isRefreshing = false
            }
        }

        binding.swipeRefreshLayoutMyCardGuest.setOnRefreshListener {
            guestViewModel.getGuests(db, auth.currentUser?.uid.toString())
        }

        binding.fabSendCard.setOnClickListener {
            findNavController().navigate(R.id.action_selectGuestForCardFragment_to_getCardMsgFragment)
        }

    }

    companion object {
    }
}