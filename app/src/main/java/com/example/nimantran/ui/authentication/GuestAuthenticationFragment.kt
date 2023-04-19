package com.example.nimantran.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentGuestAuthenticationBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GuestAuthenticationFragment : Fragment() {
    private var _binding: FragmentGuestAuthenticationBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val guestResponseViewModel: GuestResponseViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_guest_authentication, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        guestResponseViewModel.loadCards(db)

        binding.buttonVerifyRespond.setOnClickListener {
            guestResponseViewModel.selectCard(binding.textInputLayoutInviteCode.text.toString())
            guestResponseViewModel.searchCardGuest(binding.textInputLayoutPhone.text.toString())
            guestResponseViewModel.selectedInvite.observe(viewLifecycleOwner) { invite ->
                if (invite != null) {
                    findNavController().navigate(R.id.action_guestAuthenticationFragment_to_invitationResponseFragment)
                }
                else {
                    Toast.makeText(context, "Invalid Invite Code or Phone Number", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    companion object {
    }
}