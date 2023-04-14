package com.example.nimantran.ui.main.clientGuests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.databinding.FragmentAddMyGuestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AddMyGuestFragment : Fragment() {

    private var _binding: FragmentAddMyGuestBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val myGuestViewModel: MyGuestViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddMyGuestBinding.inflate(inflater, container, false)
        binding.myGuestViewModel = myGuestViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = binding.TextViewEditHouseNo.text.toString()
            .trim() + ", " + binding.TextViewEditStreet.text.toString()
            .trim() + ", " + binding.TextViewEditCity.text.toString()
            .trim() + ", " + binding.TextViewEditState.text.toString()
            .trim() + ", " + binding.TextViewEditPincode.text.toString().trim()
        val id = myGuestViewModel.getUid()
        val clientId = auth.currentUser!!.uid

        binding.buttonAddGuest.setOnClickListener {
            binding.addGuestContainer.isEnabled = false
            myGuestViewModel.saveGuest(
                db,
                binding.TextViewEditName.text.toString().trim(),
                binding.TextViewEditPhone.text.toString().trim(),
                address,
                id,
                clientId
            )
        }

        myGuestViewModel.isSaved.observe(viewLifecycleOwner) { state ->
            if (state) {
                myGuestViewModel.resetSaveStatus()
                findNavController().navigateUp() // Navigate back to MyGuestFragment
                myGuestViewModel.getGuests(db, auth.currentUser?.uid)
            } else {
                binding.addGuestContainer.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val COLL_MY_GUESTS = "myGuests"
    }

}