package com.example.nimantran.ui.admin.gift

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentAddGiftBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class AddGiftFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentAddGiftBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private val viewModel: GiftViewModel by activityViewModels()
    private var imageUri: Uri? = null
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
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_gift, container, false)
        binding.viewModelAddGift = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isSaved.observe(viewLifecycleOwner) { state ->
            if (state) {
                findNavController().navigateUp() // Navigate back to GiftListFragment
            } else {
                binding.addGiftContainer.isEnabled = true
                binding.buttonSaveGift.text = "Save"
            }
        }
        binding.apply {
            buttonSaveGift.setOnClickListener {
                addGiftContainer.isEnabled = false
                val item = editTextItemName.text.toString().trim()
                val price = editTextItemPrice.text.toString().trim()
                val quantity = editTextItemQuantity.text.toString().trim()
                val description = editTextItemDescription.text.toString().trim()
                buttonSaveGift.text = "Saving..."
                viewModel.saveGift(
                    requireContext(),
                    storage,
                    db,
                    item,
                    price,
                    quantity,
                    description,
                    imageUri
                )  // Save gift to Firestore
            }
            viewModel.isSaved.observe(viewLifecycleOwner) { state ->
                if (state) {
                    //findNavController().navigateUp() // Navigate back to NotificationListFragment
                    viewModel.getGifts(db)
                } else {
                    binding.addGiftContainer.isEnabled = true }
            }
            imageViewEditGift.setOnClickListener { selectImage() }
            imageViewBackFromAddGift.setOnClickListener {
                findNavController().navigateUp()
                viewModel.getGifts(db)
            }
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            binding.ImageViewGift.load(uri) {
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
                this, getString(R.string.permission_required),
                122, READ_EXTERNAL_STORAGE
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
        const val REQUEST_IMAGE_GET = 12
    }
}