package com.example.nimantran.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentInvitationResponseBinding
import com.google.firebase.firestore.FirebaseFirestore

class InvitationResponseFragment : Fragment() {
    private var _binding: FragmentInvitationResponseBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInvitationResponseBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
    }
}