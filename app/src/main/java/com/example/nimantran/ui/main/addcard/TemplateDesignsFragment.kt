package com.example.nimantran.ui.main.addcard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.adapters.TemplateAdapter
import com.example.nimantran.databinding.FragmentTemplateDesignsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TemplateDesignsFragment : Fragment() {

    private var _binding: FragmentTemplateDesignsBinding? = null
    private val binding get() = _binding!!
    private val viewModel:TemplateCardViewModel by activityViewModels()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTemplateDesignsBinding.inflate(inflater, container, false)
        binding.templateViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTemplates(db)
        viewModel.templates.observe(viewLifecycleOwner) {
            binding.apply {
                templateDesignsRecyclerView.adapter = TemplateAdapter(){
                    viewModel.selectTemplate(it)
                    findNavController().navigate(R.id.action_templateDesignsFragment_to_cardEditorFragment)
                }
                (templateDesignsRecyclerView.adapter as TemplateAdapter).submitList(it)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}