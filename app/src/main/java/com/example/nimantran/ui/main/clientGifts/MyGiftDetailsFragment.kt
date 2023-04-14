package com.example.nimantran.ui.main.clientGifts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.nimantran.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyGiftDetailsFragment : BottomSheetDialogFragment() {
    private var _binding: com.example.nimantran.databinding.FragmentMyGiftDetailsBinding? = null
    private val args: MyGiftDetailsFragmentArgs by navArgs()
    private val binding get() = _binding!!
    private val viewModel: MyGiftsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_gift_details, container, false)
        binding.myGiftsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedMyGift.observe(viewLifecycleOwner) {
        }
        binding.buttonOrder.setOnClickListener {
            //navigate to selectMyGuestsFragment
            findNavController().navigate(R.id.action_myGiftDetailsFragment_to_selectGuestForGiftFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val COLL_MY_GIFTS = "gifts"
    }
}