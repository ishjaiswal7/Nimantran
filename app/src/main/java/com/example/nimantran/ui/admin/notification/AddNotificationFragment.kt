package com.example.nimantran.ui.admin.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentAddNotificationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddNotificationFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val viewModel : NotificationListViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_notification, container, false)
        binding.viewModelAddNotification = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSend.setOnClickListener{
            binding.addNotificationContainer.isEnabled = false
            val subject = binding.editTextNotificationSubject.text.toString().trim()
            val body = binding.editTextNotificationBody.text.toString().trim()
            viewModel.saveNotification(db, subject, body)
        }
        viewModel.isSaved.observe(viewLifecycleOwner) { state ->
            if (state) {
                viewModel.resetState()
                findNavController().navigateUp() // Navigate back to NotificationListFragment
                viewModel.getNotifications(db)
            } else {
                binding.addNotificationContainer.isEnabled = true }
        }
        binding.apply {
        }
    }
}