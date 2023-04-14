package com.example.nimantran.ui.main.clientGifts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nimantran.adapters.SelectedGuestForGiftAdapter
import com.example.nimantran.databinding.FragmentGetOrderDetailsBinding
import com.example.nimantran.models.user.MyOrder
import com.example.nimantran.ui.main.clientGuests.MyGuestViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GetOrderDetailsFragment : Fragment() {
    private var _binding: FragmentGetOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val myGiftsViewModel: MyGiftsViewModel by activityViewModels()
    private val myGuestViewModel: MyGuestViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGetOrderDetailsBinding.inflate(inflater, container, false)
        binding.vmGifts = myGiftsViewModel
        binding.vmGuests = myGuestViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Show number of selected guests
        var numberOfGuest = 1
        val giftPrice = myGiftsViewModel.selectedMyGift.value?.price
        val qty = binding.textViewQuantity.text.toString().toInt()

//Sending gift to user
        if (myGiftsViewModel.giftForMe) {
            binding.containerOrderForMe.visibility = View.VISIBLE
            binding.containerOrderForGuest.visibility = View.GONE
            if (giftPrice != null) {
                binding.textViewTotPayableAmount.text = (giftPrice * qty).toString()
            }
        }
//Sending gift to guests
        else {
            binding.containerOrderForMe.visibility = View.GONE
            binding.containerOrderForGuest.visibility = View.VISIBLE

            myGuestViewModel.selectedGuest.observe(viewLifecycleOwner) { guest ->
                if (guest.isNotEmpty()) {
                    numberOfGuest = guest.size
                    binding.textViewNumberOfGuest.text = numberOfGuest.toString()
                    if (giftPrice != null) {
                        binding.textViewTotPayableAmount.text =
                            (giftPrice * numberOfGuest).toString()
                    }
                }
            }


            //Show selectedguest in recycler view
            myGuestViewModel.selectedGuest.observe(viewLifecycleOwner) { guest ->
                if (guest.isNotEmpty()) {
                    val selectedGuestForGiftAdapter =
                        SelectedGuestForGiftAdapter(requireActivity()) {}
                    binding.recyclerViewSelectedMyGuestForGift.adapter = selectedGuestForGiftAdapter
                    selectedGuestForGiftAdapter.submitList(guest.toList())
                }
            }
            binding.buttonOrderGift.setOnClickListener {
                MyOrder(
                    clientID = auth.currentUser?.uid.toString(),
                    orderStatus = "Placed",
                    totalAmount = giftPrice!! * numberOfGuest,
                    giftQty = numberOfGuest.toString(),
                    gift = myGiftsViewModel.selectedMyGift.value!!,
                    guest = myGuestViewModel.selectedGuest.value!!.toList(),
                    paymentRefId = "1234567890",
                    sentTo = "Guest",
                ).let {
                    myGiftsViewModel.addOrder(db, auth, it)
                }
            }
            Log.d("abc", myGuestViewModel.selectedGuest.value.toString())
            Log.d("gift", myGiftsViewModel.selectedMyGift.value.toString())

        }
    }

}