package com.example.nimantran.ui.admin.gift

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentEditGiftBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class EditGiftFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentEditGiftBinding? = null
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
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_gift, container, false)
        binding.viewModelEditGift = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.getGifts(db)

        Log.d("TAG", "onViewCreated: ${viewModel.selectedGift.value?.item}")
        Log.d("TAGX", "onViewCreated: ${viewModel.selectedGift.value}")


        viewModel.isSaved.observe(viewLifecycleOwner) { state ->
            if (state) {
                findNavController().navigateUp() // Navigate back to GiftListFragment
                viewModel.getGifts(db)
            } else {
                binding.editGiftContainer.isEnabled = true
                binding.btnSaveGift.text = "Save"
            }
        }

        disableEdit()


        binding.imageViewDelete.setOnClickListener {
            Toast.makeText(context, "Gift Deleted", Toast.LENGTH_SHORT).show()
            viewModel.deleteGift(db)

            findNavController().navigateUp()
            viewModel.getGifts(db)
        }

        binding.btnEditGift.setOnClickListener {
            enableEdit()
            Toast.makeText(context, "Edit Mode", Toast.LENGTH_SHORT).show()

        }

        binding.apply {
            btnSaveGift.setOnClickListener {
                editGiftContainer.isEnabled = false

                val item = editGiftName.text.toString().trim()
                val price = editGiftPrice.text.toString().trim()
                val quantity = editGiftQuantity.text.toString().trim()
                val description = editGiftDescription.text.toString().trim()

                btnSaveGift.text = "Saving..."
                viewModel.updateGift(
                    db,
                    item,
                    price,
                    quantity,
                    description,
                )  // Save gift to Firestore


                //To Disable Edit Mode after saving
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                disableEdit()
                btnSaveGift.text = "Save"

            }
        }

        binding.btnCancel.setOnClickListener {
                Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()
                disableEdit()
        }

        binding.imageViewBackToGiftList.setOnClickListener {
            viewModel.getGifts(db)
            findNavController().navigateUp()
            viewModel.getGifts(db)
        }

    }
    private fun disableEdit() {
        binding.editGiftName.isEnabled = false
        binding.editGiftDescription.isEnabled = false
        binding.editGiftPrice.isEnabled = false
        binding.editGiftQuantity.isEnabled = false
        binding.btnSaveGift.isVisible = false
        binding.btnCancel.isVisible = false
        binding.btnEditGift.isVisible = true
    }

    private fun enableEdit() {
        binding.editGiftName.isEnabled = true
        binding.editGiftDescription.isEnabled = true
        binding.editGiftPrice.isEnabled = true
        binding.editGiftQuantity.isEnabled = true
        binding.btnSaveGift.isVisible = true
        binding.btnCancel.isVisible = true
        binding.btnEditGift.isVisible = false
    }

        private val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                binding.ImageViewGift.load(uri) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
                imageUri = uri
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

    }
}
