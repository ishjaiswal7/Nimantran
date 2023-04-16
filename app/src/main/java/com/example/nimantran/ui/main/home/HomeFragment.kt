package com.example.nimantran.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.adapters.MyCardsAdapter
import com.example.nimantran.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewStartDesigning.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_templateDesignsFragment)
        }
        binding.imageView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_templateDesignsFragment)
        }
        binding.imageView2.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_templateDesignsFragment)
        }
        binding.imageView3.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_templateDesignsFragment)
        }
        binding.imageView4.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_myGiftsFragment)
        }
        binding.imageView5.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_myGiftsFragment)
        }
        binding.imageView6.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_myGiftsFragment)
        }
        binding.textViewOrderGift.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_myGiftsFragment)
        }

    }

}