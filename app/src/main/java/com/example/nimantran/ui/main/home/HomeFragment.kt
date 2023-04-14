package com.example.nimantran.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.nimantran.R
import com.example.nimantran.adapters.MyCardsAdapter
import com.example.nimantran.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getMyCards(db) // fetch data only
        homeViewModel.mycards.observe(viewLifecycleOwner) { mycards ->
            if (binding.swipeRefreshLayoutHome.isRefreshing) {
                binding.swipeRefreshLayoutHome.isRefreshing = false
            }
        }
        binding.swipeRefreshLayoutHome.setOnRefreshListener {
            homeViewModel.getMyCards(db)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    companion object {
        const val COLL_MYCARDS = "mycards"
    }
}