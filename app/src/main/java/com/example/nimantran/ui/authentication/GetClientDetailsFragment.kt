package com.example.nimantran.ui.authentication

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.nimantran.MainActivity
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentGetClientDetailsBinding
import com.example.nimantran.models.admin.Client
import com.example.nimantran.ui.admin.gift.AddGiftFragment.Companion.REQUEST_IMAGE_GET
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class GetClientDetailsFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentGetClientDetailsBinding? = null
    private val binding get() = _binding!!
    private val prefs by lazy { requireActivity().getSharedPreferences("prefs", 0) }
    private lateinit var auth: FirebaseAuth
    private lateinit var clientId: String
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private val viewModel: ClientViewModel by activityViewModels()
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGetClientDetailsBinding.inflate(inflater, container, false)
        binding.getClientDetailsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fill the fields if user is already logged in
        val currUser = auth.currentUser
        if (currUser != null) {
            clientId = currUser.uid
            currUser.phoneNumber?.let { binding.TextViewSetPhone.setText(it) }

        }

        viewModel.isSaved.observe(viewLifecycleOwner) { state ->
            if (state) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                makeText(
                    requireContext(),
                    "Please fill all the fields",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.textViewPrivacyPolicy.setOnClickListener {
            findNavController().navigate(R.id.action_getDetailsFragment_to_privacyPolicyFragment)
        }

        binding.buttonSave.setOnClickListener {
            if (!binding.checkBoxPrivacyPolicy.isChecked) {
                makeText(
                    requireContext(),
                    "Please accept the privacy policy",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                clientId = currUser!!.uid
                val name = binding.TextViewSetName.text.toString()
                val phone = binding.TextViewSetPhone.text.toString()
                val gender = when (binding.genderGroup.checkedRadioButtonId) {
                    R.id.radioFemale -> "Female"
                    R.id.radioMale -> "Male"
                    else -> "Unknown"
                }
                // add validations
                if (name.isEmpty()) {
                    binding.TextViewSetName.error = "Please enter your name"
                    binding.TextViewSetName.requestFocus()
                    return@setOnClickListener
                }

                viewModel.saveClient(
                    requireContext(),
                    storage,
                    db,
                    clientId,
                    name,
                    phone,
                    gender,
                    imageUri
                ) // save client details to firestore

/*

                val client= Client(clientId, name, phone, gender)
                db.collection(COLL_CLIENT).document(clientId).set(client).addOnSuccessListener {
                    prefs.edit().putBoolean("isFirstTime", false).apply()
                    // jump to main activity
                    startActivity(Intent(activity, MainActivity::class.java))
                    requireActivity().finish()
                }.addOnFailureListener {
                    makeText(
                        requireContext(),
                        "Something went wrong. Please try again later ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
*/
            }
        }

        binding.imageViewEditUser.setOnClickListener {
            selectImage()
        }

        binding.imageViewBackFromAddDetails.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            binding.ImageViewUser.load(uri) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
            imageUri = uri
        }

    @AfterPermissionGranted(REQUEST_IMAGE_GET)
    private fun selectImage() {
        if (EasyPermissions.hasPermissions(
                requireContext(),
                READ_EXTERNAL_STORAGE,
            )
        ) {
            getContent.launch("image/*")
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Please grant the permission to access your gallery",
                REQUEST_IMAGE_GET,
                READ_EXTERNAL_STORAGE,
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
        selectImage()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val COLL_CLIENT = "clients"
    }
}