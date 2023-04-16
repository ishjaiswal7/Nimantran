package com.example.nimantran.ui.main.clientGuests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.databinding.FragmentEditMyGuestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class EditMyGuestFragment : Fragment() {
    private var _binding: FragmentEditMyGuestBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private val viewModel: MyGuestViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        storage = Firebase.storage
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditMyGuestBinding.inflate(inflater, container, false)
        binding.myGuestViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isSaved.observe(viewLifecycleOwner) {state->
            if (state) {
                findNavController().navigateUp()
                viewModel.getGuests(db, auth.currentUser?.uid.toString())
            } else{
                binding.editMyGuestContainer.isEnabled = true
                disableEditing()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun disableEditing(){
        /*
        binding.btnSaveGuest.isVisible = false
        binding.btnCancelEdit.isVisible = false
        binding.btnEditGuest.isVisible = true
    */
    }


    private fun enableEditing(){

    }
    companion object {
    }
}