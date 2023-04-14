package com.example.nimantran.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nimantran.AdminActivity
import com.example.nimantran.MainActivity
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentGetInBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class GetInFragment : Fragment() {
    private var _binding: FragmentGetInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var phoneNumber: String
    private val prefs by lazy { requireActivity().getSharedPreferences("prefs", 0) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("en")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // User is signed in (getCurrentUser() will be null if not signed in)
        //sendToMain()

        binding.progressBar.visibility = View.GONE

        binding.buttonVerify.setOnClickListener {
            phoneNumber = "+91" + binding.editPhone.text.toString().trim()
            if (phoneNumber.length == 13) {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(requireActivity())                 // Activity (for callback binding)
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
                binding.progressBar.visibility = View.VISIBLE
                binding.buttonVerify.isEnabled = false
                binding.editPhone.isEnabled = false
            } else {
                Toast.makeText(activity, "Please enter a valid phone number", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.textViewResponse.setOnClickListener {
            findNavController().navigate(R.id.action_getInFragment_to_guestAuthenticationFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            Log.d("TAG", "onStart: ${auth.currentUser}")
            Log.d("TAG", prefs.getString("userType", "user").toString())
            when (prefs.getString("userType", "user")) {
                "user" -> {
                    Log.d("TAG", "onStart: ${auth.currentUser}")
                    Log.d("TAG", prefs.getString("userType", "user").toString())

                    startActivity(Intent(activity, MainActivity::class.java))
                    activity?.finish()

                }
                "admin" -> {
                    startActivity(Intent(activity, AdminActivity::class.java))
                    activity?.finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            // Client is signed in (getCurrentUser() will be null if not signed in)
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGetInBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun sendToMain() {
        startActivity(Intent(activity, MainActivity::class.java))
        activity?.finish()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    binding.progressBar.visibility = View.GONE
                    binding.buttonVerify.isEnabled = true
                    binding.editPhone.isEnabled = true
                    Toast.makeText(activity, "Signed In", Toast.LENGTH_SHORT).show()
                    sendToMain()
                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Toast.makeText(activity, "Sign In Failed", Toast.LENGTH_SHORT).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(activity, "Invalid OTP", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        binding.buttonVerify.isEnabled = true
                        binding.editPhone.isEnabled = true
                    }
                    // Update UI
                }
            }
    }

    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(activity, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(activity, "SMS Quota Exceeded", Toast.LENGTH_SHORT).show()
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later

            Log.d("TAG", "onCodeSent:$verificationId")
            Log.d("TAG", "onCodeSent:$token")
            Log.d("TAG", "onCodeSent:$phoneNumber")
//             get otp
            val dir = GetInFragmentDirections.actionGetInFragmentToOTPFragment(
                verificationId,
                phoneNumber,
                token
            )
            findNavController().navigate(dir)
        }
    }

    companion object {
    }
}