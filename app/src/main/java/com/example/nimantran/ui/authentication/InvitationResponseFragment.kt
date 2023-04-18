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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_invitation_response, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewCardTitle.text = guestResponseViewModel.inviteCardTitle
        binding.textViewEventDate.text = guestResponseViewModel.inviteEventDate
        binding.textViewCardMessage.text = guestResponseViewModel.inviteMessage
        binding.textViewRSVP.text = guestResponseViewModel.inviteRSVP
        binding.textViewPhone.text = guestResponseViewModel.invitePhone
        //Set inviteCardImage in the image view
        binding.textViewTapToRespond.setOnClickListener {
            binding.buttonNo.visibility = View.VISIBLE
            binding.buttonYes.visibility = View.VISIBLE
            binding.buttonMayBe.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        guestResponseViewModel.resetCardDetails()
        super.onDestroyView()
        _binding = null
    }
    companion object {
    }
}