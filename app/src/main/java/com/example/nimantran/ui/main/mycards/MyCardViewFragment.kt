package com.example.nimantran.ui.main.mycards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentMyCardViewBinding
import com.example.nimantran.ui.main.addcard.TemplateCardViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyCardViewFragment : Fragment() {
    private var _binding: FragmentMyCardViewBinding? = null
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
        _binding = FragmentMyCardViewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
    }
}