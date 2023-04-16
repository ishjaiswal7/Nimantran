package com.example.nimantran.ui.main.mycards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.nimantran.databinding.FragmentMyCardsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.nimantran.ui.main.addcard.TemplateCardViewModel

class MyCardsFragment : Fragment() {
    private var _binding: FragmentMyCardsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val templateCardViewModel: TemplateCardViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCardsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        templateCardViewModel.getMyCards(db)
        templateCardViewModel.mycards.observe(viewLifecycleOwner) { mycards ->
            if (binding.swipeRefreshLayoutMyCards.isRefreshing) {
                binding.swipeRefreshLayoutMyCards.isRefreshing = false
            }
        }
        binding.swipeRefreshLayoutMyCards.setOnRefreshListener {
            templateCardViewModel.getMyCards(db)
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