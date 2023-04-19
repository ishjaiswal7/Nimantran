package com.example.nimantran.ui.main.mycards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.adapters.MyCardsAdapter
import com.example.nimantran.databinding.FragmentMyCardsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth

class MyCardsFragment : Fragment() {
    private var _binding: FragmentMyCardsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val myCardsViewModel: MyCardsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
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
        myCardsViewModel.getMyCards(db, auth.currentUser?.uid)
        myCardsViewModel.myCards.observe(viewLifecycleOwner) { myCards ->
            if(myCards.isNotEmpty()){
                binding.recyclerViewMyCards.adapter =
                    MyCardsAdapter(requireActivity()){
                        myCardsViewModel.selectMyCard(it)
                        findNavController().navigate(R.id.action_myCardsFragment_to_myCardViewFragment)
                    }
                (binding.recyclerViewMyCards.adapter as MyCardsAdapter).submitList(
                    myCards
                )
            }else{
                binding.recyclerViewMyCards.visibility = View.GONE
                binding.textViewNoData.visibility = View.VISIBLE
                binding.imageViewNoData.visibility = View.VISIBLE
            }
            if (binding.swipeRefreshLayoutMyCards.isRefreshing) {
                binding.swipeRefreshLayoutMyCards.isRefreshing = false
            }
        }
        binding.swipeRefreshLayoutMyCards.setOnRefreshListener {
            myCardsViewModel.getMyCards(db, auth.currentUser?.uid)
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