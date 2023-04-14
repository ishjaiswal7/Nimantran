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
import com.example.nimantran.ui.main.clientGuests.MyGuestViewModel

class GetOrderDetailsFragment : Fragment() {
    private var _binding: FragmentGetOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private val myGiftsViewModel: MyGiftsViewModel by activityViewModels()
    private val myGuestViewModel: MyGuestViewModel by activityViewModels()
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

//Sending gift to user
        if (myGiftsViewModel.giftForMe) {
            binding.containerOrderForMe.visibility = View.VISIBLE
            binding.containerOrderForGuest.visibility = View.GONE
            binding.textViewTotPayableAmount.text = giftPrice.toString()
        }
//Sending gift to guests
        else {
            binding.containerOrderForMe.visibility = View.GONE
            binding.containerOrderForGuest.visibility = View.VISIBLE

            myGuestViewModel.selectedGuest.observe(viewLifecycleOwner){guest->
                if(guest.isNotEmpty()){
                    numberOfGuest = guest.size
                    binding.textViewNumberOfGuest.text = numberOfGuest.toString()
                    if (giftPrice != null) {
                        binding.textViewTotPayableAmount.text = (giftPrice * numberOfGuest).toString()
                    }
                }
            }


            //Show selectedguest in recycler view
            myGuestViewModel.selectedGuest.observe(viewLifecycleOwner){guest->
                if(guest.isNotEmpty()){
                    binding.recyclerViewSelectedMyGuestForGift.adapter =
                        SelectedGuestForGiftAdapter(requireActivity()){

                        }
                }

            }


            Log.d("abc", myGuestViewModel.selectedGuest.value.toString())
            Log.d("gift", myGiftsViewModel.selectedMyGift.value.toString())

        }
    }

}