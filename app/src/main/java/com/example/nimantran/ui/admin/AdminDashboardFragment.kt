package com.example.nimantran.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.nimantran.AuthenticationActivity
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentAdminDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val prefs by lazy { requireActivity().getSharedPreferences("prefs", 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userManagement.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_userListFragment)
        }

        binding.orderManagement.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_orderListFragment)
        }

        binding.appInsight.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_appInsightFragment)
        }

        binding.giftManagement.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_giftListFragment)
        }

        binding.notificationManagement.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_notificationListFragment)
        }


        binding.logout.setOnClickListener {
            auth.signOut()
            prefs.edit().putString("userType", "").apply()
            startActivity(Intent(activity, AuthenticationActivity::class.java))
            activity?.finish()
        }
    }

    companion object {

    }
}