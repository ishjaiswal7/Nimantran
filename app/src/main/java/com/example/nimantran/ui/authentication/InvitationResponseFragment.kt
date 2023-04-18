package com.example.nimantran.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentInvitationResponseBinding
import com.google.firebase.firestore.FirebaseFirestore

class InvitationResponseFragment : Fragment() {
    private var _binding: FragmentInvitationResponseBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val guestResponseViewModel: GuestResponseViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_invitation_response, container, false)
        binding.invitationResponseViewModel = guestResponseViewModel
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(guestResponseViewModel.selectedInvite.value?.response == "Not Responded"){
            responseButtonVisible()
        }else{
            responseButtonInvisible()
            binding.textViewTapToRespond.text = "Tap to Edit Response"
        }
        //Set inviteCardImage in the image view
        binding.textViewTapToRespond.setOnClickListener {
            responseButtonVisible()
        }
        binding.buttonYes.setOnClickListener {
            //update response in firebase by viewModel
            guestResponseViewModel.updateMyCardInviteResponse(db, "Yes")
            responseButtonInvisible()
        }
        binding.buttonNo.setOnClickListener {
            //update response in firebase by viewModel
            guestResponseViewModel.updateMyCardInviteResponse(db, "No")
            responseButtonInvisible()
        }
        binding.buttonMayBe.setOnClickListener {
            //update response in firebase by viewModel
            guestResponseViewModel.updateMyCardInviteResponse(db, "May Be")
            responseButtonInvisible()
        }
    }


    private fun responseButtonVisible(){
        binding.buttonNo.visibility = View.VISIBLE
        binding.buttonYes.visibility = View.VISIBLE
        binding.buttonMayBe.visibility = View.VISIBLE
        binding.textViewTapToRespond.visibility = View.GONE
    }
    private fun responseButtonInvisible(){
        binding.buttonNo.visibility = View.GONE
        binding.buttonYes.visibility = View.GONE
        binding.buttonMayBe.visibility = View.GONE
        binding.textViewTapToRespond.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
    }
}