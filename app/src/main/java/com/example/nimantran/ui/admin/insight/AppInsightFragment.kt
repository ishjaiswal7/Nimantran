package com.example.nimantran.ui.admin.insight

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.nimantran.R
import com.example.nimantran.R.layout.fragment_app_insight
import com.example.nimantran.databinding.FragmentAppInsightBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppInsightFragment : Fragment() {

    private var _binding: FragmentAppInsightBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val appInsightViewModel: AppInsightViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, fragment_app_insight, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appInsightViewModel.getClients(db)
        appInsightViewModel.clients.observe(viewLifecycleOwner) { clients ->
            binding.textViewTotalClients.text = clients.size.toString()
        }
    }

    companion object {
        const val COLL_CLIENTS = "clients"
    }
}