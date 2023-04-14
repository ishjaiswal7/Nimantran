package com.example.nimantran.ui.main.addcard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentCardEditorBinding
import com.example.nimantran.getTempFileUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CardEditorFragment : Fragment() {
    private var _binding: FragmentCardEditorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TemplateCardViewModel by activityViewModels()
    private lateinit var db: FirebaseFirestore
    private var mUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
    }

    // DS Editor Launcher
    private val photoEditorLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                // display result in the ivTemplate
                mUri = it.data
                binding.ivTemplate.setImageURI(mUri)

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCardEditorBinding.inflate(inflater, container, false)
        binding.templateCardViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedTemplate.value?.let {
            mUri = Uri.parse(it.url)
            binding.ivTemplate.setImageURI(Uri.parse(it.url))
            launchPhotoEditor(Uri.parse(it.url))

            binding.cLEdit.setOnClickListener {
                launchPhotoEditor(mUri!!)
            }
        }
        binding.cLShare.setOnClickListener {
            findNavController().navigate(R.id.action_cardEditorFragment_to_selectGuestForCardFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchPhotoEditor(uri: Uri) {
        val photoEditorIntent = Intent(requireContext(), DsPhotoEditorActivity::class.java)
        photoEditorIntent.data = uri
        photoEditorIntent.putExtra(
            DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,
            "Nimantran"
        );
        photoEditorLauncher.launch(photoEditorIntent)
    }

    /**
     * Method will launch Content Launcher to choose images.
     */
    private fun launchGallery() {
        selectImageLauncher.launch("image/*") // Launch the selectImageLauncher with "image/*" type(only images).
    }

    /**
     * Method will launch Camera to take picture.
     */
    private fun launchCamera() {
        mUri = getTempFileUri(requireContext())
        cameraLauncher.launch(mUri) // Launch camera with the temp uri.
    }

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                launchPhotoEditor(it)
            }
        }


    // Camera Launcher.
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                mUri?.let {
                    launchPhotoEditor(it)
                }
            }
        }
}