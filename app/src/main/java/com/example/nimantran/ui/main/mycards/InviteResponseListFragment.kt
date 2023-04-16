package com.example.nimantran.ui.main.mycards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.nimantran.R
import com.example.nimantran.adapters.user.InviteResponseListAdapter
import com.example.nimantran.databinding.FragmentInviteResponseListBinding
import com.example.nimantran.ui.main.addcard.TemplateCardViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InviteResponseListFragment : Fragment() {
    private var _binding: FragmentInviteResponseListBinding? = null
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
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invite_response_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.recyclerViewInviteResponseList.adapter = InviteResponseListAdapter(context, templateCardViewModel)
        binding.swipeRefreshLayoutInviteResponseList.setOnRefreshListener {
            binding.swipeRefreshLayoutInviteResponseList.isRefreshing = false
        }

    }

    companion object {
    }
}