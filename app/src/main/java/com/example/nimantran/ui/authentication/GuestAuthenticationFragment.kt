package com.example.nimantran.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentGuestAuthenticationBinding
import com.google.firebase.firestore.FirebaseFirestore

class GuestAuthenticationFragment : Fragment() {
    private var _binding: FragmentGuestAuthenticationBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGuestAuthenticationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonVerifyRespond.setOnClickListener {
            findNavController().navigate(R.id.action_guestAuthenticationFragment_to_invitationResponseFragment)
        }
    }
    companion object {
    }
}