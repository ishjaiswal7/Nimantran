package com.example.nimantran.ui.main.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentMyProfileBinding
import com.example.nimantran.models.admin.Client
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import pub.devrel.easypermissions.EasyPermissions

class MyProfileFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private val myProfileViewModel: MyProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        binding.myProfileViewModel = myProfileViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disableEdit()

        val currUser = auth.currentUser
/*
        if (currUser != null) {
            userId = currUser.uid
            currUser.phoneNumber?.let { binding.editTextEditPhone.setText(it) }
        }
*/
        myProfileViewModel.getClient(db, currUser?.uid)



        /*
        binding.imageViewUser.load(client.image) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
*/
        binding.buttonEditUser.setOnClickListener {
            enableEdit()
        }

        binding.buttonSaveEditUser.setOnClickListener {
            disableEdit()
            userId = currUser!!.uid
            val name = binding.editTextEditName.text.toString().trim()
            val phone = binding.editTextEditPhone.text.toString().trim()
            val gender = when (binding.genderGroup.checkedRadioButtonId) {
                R.id.radioFemale -> "Female"
                R.id.radioMale -> "Male"
                else -> "Unknown"
            }

            if (name.isEmpty()) {
                binding.editTextEditName.error = "Please enter your name"
                binding.editTextEditName.requestFocus()
                return@setOnClickListener
            }




            myProfileViewModel.updateClient(db, name, gender)
        }
/*
        binding.imageViewEditUser.setOnClickListener {
            selectImage()
        }
*/
        binding.buttonCancelEditUser.setOnClickListener {
            disableEdit()
            myProfileViewModel.getClient(db, auth.currentUser?.uid)
        }


    }

    /*
        private val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                binding.ImageViewUser.load(uri) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                    myProfileViewModel.uploadToFirebase(requireActivity(), storage, uri)
                }
            }


        @AfterPermissionGranted(AddGiftFragment.REQUEST_IMAGE_GET)
        private fun selectImage() {
            if (EasyPermissions.hasPermissions(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            ) {
                getContent.launch("image/*")
            } else {
                EasyPermissions.requestPermissions(
                    this, getString(R.string.permission_required),
                    122, Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        }
    */

     */
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
//        selectImage()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null
    }

    private fun enableEdit() {
        binding.apply {
            buttonEditUser.isVisible = false
            buttonSaveEditUser.isVisible = true
            buttonCancelEditUser.isVisible = true
            imageViewEditUser.isVisible = true
            editTextEditName.isEnabled = true
//            editTextEditPhone.isEnabled = true
            genderGroup.isEnabled = true
            radioFemale.isEnabled = true
            radioMale.isEnabled = true
        }
    }

    private fun disableEdit() {
        binding.apply {
            buttonEditUser.isVisible = true
            buttonSaveEditUser.isVisible = false
            buttonCancelEditUser.isVisible = false
            imageViewEditUser.isVisible = false
            editTextEditName.isEnabled = false
            editTextEditPhone.isEnabled = false
            genderGroup.isEnabled = false
            radioFemale.isEnabled = false
            radioMale.isEnabled = false
        }
    }

    companion object {
        const val COLL_CLIENT = "clients"
    }
}