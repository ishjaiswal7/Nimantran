package com.example.nimantran.ui.main.clientGifts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.adapters.SelectGuestAdapter
import com.example.nimantran.databinding.FragmentSelectMyGuestForGiftBinding
import com.example.nimantran.ui.main.clientGuests.MyGuestViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SelectGuestForGiftFragment : Fragment() {
    private var _binding: FragmentSelectMyGuestForGiftBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val myGiftsViewModel: MyGiftsViewModel by activityViewModels()
    private val myGuestViewModel: MyGuestViewModel by activityViewModels()
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
            R.layout.fragment_select_my_guest_for_gift,
            container,
            false
        )
        binding.myGiftsViewModel = myGiftsViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myGuestViewModel.getGuests(db, auth.currentUser?.uid.toString())
        binding.buttonSendGift.setOnClickListener {
            //Send gift to me
            if (binding.radioButtonSendToMe.isChecked) {
                val qty = binding.textViewUserGiftQty.text.toString()
                if (qty == "") {
                    Toast.makeText(context, "invalid quantity.", Toast.LENGTH_SHORT).show()
                } else {
                    myGiftsViewModel.userGiftQty = qty.toInt()
                    myGiftsViewModel.userAddress =
                        binding.TextViewEditHouseNo.text.toString() + ", " +
                                binding.TextViewEditStreet.text.toString() + ", " +
                                binding.TextViewEditCity.text.toString() + ", " +
                                binding.TextViewEditState.text.toString() + ", " +
                                binding.TextViewEditPincode.text.toString()
                    myGiftsViewModel.sendToMe()
                    findNavController().navigate(R.id.action_selectGuestForGiftFragment_to_getOrderDetailsFragment)
                }
            }
            //Send gift to guest
            else if (binding.radioButtonSendToGuest.isChecked) {
                var numberOfGuest = 0
                //Count number of selected guest
                myGuestViewModel.selectedGuest.observe(viewLifecycleOwner) { guest ->
                    if (guest.isNotEmpty()) {
                        numberOfGuest = guest.size
                        if (numberOfGuest == 0) {
                            Toast.makeText(context, "Select a guest.", Toast.LENGTH_SHORT).show()
                        } else
                            myGiftsViewModel.sendToGuest()
                        findNavController().navigate(R.id.action_selectGuestForGiftFragment_to_getOrderDetailsFragment)
                    }
                }
            } else {
                Toast.makeText(context, "Select an option above.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.radioButtonSendToMe.setOnClickListener {
            if (binding.radioButtonSendToMe.isChecked) {
                binding.radioButtonSendToGuest.isChecked = false
                //make containerSendToMe visible
                binding.containerSendToMe.visibility = View.VISIBLE
                binding.containerSendToGuest.visibility = View.GONE
                binding.buttonSendGift.isEnabled = true
            } else {
                binding.radioButtonSendToGuest.callOnClick()
            }
        }
        binding.radioButtonSendToGuest.setOnClickListener {
            if (binding.radioButtonSendToGuest.isChecked) {
                binding.radioButtonSendToMe.isChecked = false
                //make containerSendToMe visible
                binding.containerSendToMe.visibility = View.GONE
                binding.containerSendToGuest.visibility = View.VISIBLE
                binding.buttonSendGift.isEnabled = true

            } else {
                binding.radioButtonSendToMe.callOnClick()

            }
        }
        myGuestViewModel.guests.observe(viewLifecycleOwner) { guests ->
            if (guests.isNotEmpty()) {
                val selectGuestAdapter =
                    SelectGuestAdapter { guest, bool -> myGuestViewModel.updateGuest(guest, bool) }
                binding.recyclerViewSelectMyGuest.adapter = selectGuestAdapter
                selectGuestAdapter.submitList(guests)
            } else {
                binding.recyclerViewSelectMyGuest.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutMyGuest.isRefreshing) {
                binding.swipeRefreshLayoutMyGuest.isRefreshing = false
            }
        }

    }

    companion object {
    }

    private fun loadGuestList() {
        //load guest list
    }

    private fun loadMyDetails() {
        //load my details
    }

    private fun removeGuestList() {
        //remove Guest List
    }

    private fun removeMyDetails() {
        //remove my details
    }
}