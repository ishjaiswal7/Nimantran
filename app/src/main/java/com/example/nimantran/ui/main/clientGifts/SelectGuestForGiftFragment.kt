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
import com.google.firebase.firestore.FirebaseFirestore

class SelectGuestForGiftFragment : Fragment() {
    private var _binding: FragmentSelectMyGuestForGiftBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val myGiftsViewModel: MyGiftsViewModel by activityViewModels()
    private val myGuestViewModel: MyGuestViewModel by activityViewModels()

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
        Toast.makeText(context, "Select an option above.", Toast.LENGTH_SHORT).show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSendGift.setOnClickListener {
            if (binding.radioButtonSendToMe.isChecked) {
                myGiftsViewModel.sendToMe()
            } else if (binding.radioButtonSendToGuest.isChecked) {
                myGiftsViewModel.sendToGuest()
            } else {
                Toast.makeText(context, "Select an option above.", Toast.LENGTH_SHORT).show()
            }
            findNavController().navigate(R.id.action_selectGuestForGiftFragment_to_getOrderDetailsFragment)
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

        /*
        myGuestViewModel.guests.observe(viewLifecycleOwner) { guests ->
            if (guests.isNotEmpty()) {
                binding.recyclerViewSelectMyGuest.adapter =
                    MyGuestListAdapter(requireActivity()) {
                        myGuestViewModel.selectGuest(it)
                        val dir =
                            MyGuestListFragmentDirections.actionMyGuestListFragmentToEditGuestFragment(
                                it.id
                            )
                        findNavController().navigate(dir)
                    }

                (binding.recyclerViewSelectMyGuest.adapter as MyGuestListAdapter).submitList(
                    guests
                )
            } else {
                binding.recyclerViewSelectMyGuest.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutMyGuest.isRefreshing) {
                binding.swipeRefreshLayoutMyGuest.isRefreshing = false
            }
        }
         */

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