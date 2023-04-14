package com.example.nimantran.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.AdminActivity
import com.example.nimantran.MainActivity
import com.example.nimantran.R
import com.example.nimantran.databinding.FragmentOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OTPFragment : Fragment() {


    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var db: FirebaseFirestore
    private lateinit var OTP: String
    private lateinit var resendingToken: ForceResendingToken
    private lateinit var phoneNumber: String
    private val prefs by lazy { requireActivity().getSharedPreferences("prefs", 0) }
    private val viewModel: ClientViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
    }

    private fun resendOtpTextViewVisibility() {
        binding.otpEditText1.setText("")
        binding.otpEditText2.setText("")
        binding.otpEditText3.setText("")
        binding.otpEditText4.setText("")
        binding.otpEditText5.setText("")
        binding.otpEditText6.setText("")
        binding.textViewResendOtp.visibility = View.INVISIBLE
        binding.textViewResendOtp.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            binding.textViewResendOtp.visibility = View.VISIBLE
            binding.textViewResendOtp.isEnabled = true
        }, 60000)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    binding.progressBar2.visibility = View.INVISIBLE
                    Toast.makeText(activity, "Signed In", Toast.LENGTH_SHORT).show()
                    val user = task.result?.user
                    sendToMain(user)
                } else {
                    // Sign in failed, display a message and update the UI
                    Toast.makeText(activity, "Sign In Failed", Toast.LENGTH_SHORT).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(activity, "Invalid OTP", Toast.LENGTH_SHORT).show()
                        binding.buttonOtp.isEnabled = true  // Enable the button
                        binding.progressBar2.visibility = View.INVISIBLE
                    }
                    // Update UI
                }
            }
    }

    private fun sendToMain(user: FirebaseUser?) {
        if (user != null) {
            val phn = user.phoneNumber
            viewModel.getClient(db, user.uid)
            viewModel.client.observe(viewLifecycleOwner) {
                Log.d("TAG", "sendToMain: $phn")
                when (phn) {
                    "+919336843452" -> {
                        prefs.edit().putString("userType", "admin").apply()
                        startActivity(Intent(activity, AdminActivity::class.java))
                        requireActivity().finish()
                    }
                    else -> {
                        prefs.edit().putString("userType", "user").apply()
                        Log.d("TAG", "sendToMain: ${prefs.getBoolean("isFirstTime", true)}")
                        if (it == null) {
                            prefs.edit().putBoolean("isFirstTime", false).apply()
                            findNavController().navigate(R.id.action_OTPFragment_to_getDetailsFragment)
                        }
/*                        if (prefs.getBoolean("isFirstTime", true)) {
                            prefs.edit().putBoolean("isFirstTime", false).apply()
                            findNavController().navigate(R.id.action_OTPFragment_to_getDetailsFragment)
                        }*/
                        else {
                            startActivity(Intent(activity, MainActivity::class.java))
                            requireActivity().finish()
                        }
                    }
                }
            }
        }
    }

    private fun addTextChangeListner() {
        binding.otpEditText1.addTextChangedListener(EditTextWatcher(binding.otpEditText1))
        binding.otpEditText2.addTextChangedListener(EditTextWatcher(binding.otpEditText2))
        binding.otpEditText3.addTextChangedListener(EditTextWatcher(binding.otpEditText3))
        binding.otpEditText4.addTextChangedListener(EditTextWatcher(binding.otpEditText4))
        binding.otpEditText5.addTextChangedListener(EditTextWatcher(binding.otpEditText5))
        binding.otpEditText6.addTextChangedListener(EditTextWatcher(binding.otpEditText6))
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            when (view.id) {
                R.id.otpEditText1 -> if (text.length == 1) binding.otpEditText2.requestFocus()
                R.id.otpEditText2 -> if (text.length == 1) binding.otpEditText3.requestFocus() else if (text.isEmpty()) binding.otpEditText1.requestFocus()
                R.id.otpEditText3 -> if (text.length == 1) binding.otpEditText4.requestFocus() else if (text.isEmpty()) binding.otpEditText2.requestFocus()
                R.id.otpEditText4 -> if (text.length == 1) binding.otpEditText5.requestFocus() else if (text.isEmpty()) binding.otpEditText3.requestFocus()
                R.id.otpEditText5 -> if (text.length == 1) binding.otpEditText6.requestFocus() else if (text.isEmpty()) binding.otpEditText4.requestFocus()
                R.id.otpEditText6 -> if (text.isEmpty()) binding.otpEditText5.requestFocus()

            }
            if (binding.otpEditText1.text.toString()
                    .isEmpty() && binding.otpEditText2.text.toString()
                    .isEmpty() && binding.otpEditText3.text.toString()
                    .isEmpty() && binding.otpEditText4.text.toString()
                    .isEmpty() && binding.otpEditText5.text.toString()
                    .isEmpty() && binding.otpEditText6.text.toString().isEmpty()
            ) {
                binding.otpEditText1.requestFocus()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_otp, container, false)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendingToken) // ForceResendingToken from callbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

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
            token: ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            OTP = verificationId
            resendingToken = token
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.otpEditText1.requestFocus()
        val bundle = OTPFragmentArgs.fromBundle(requireArguments())
        phoneNumber = bundle.phone
        OTP = bundle.otp
        resendingToken = bundle.token
        init()
        addTextChangeListner()
        resendOtpTextViewVisibility()
        binding.progressBar2.visibility = View.INVISIBLE

        binding.textViewPhone.text = bundle.phone

        binding.buttonOtp.setOnClickListener {
            val typedOTP =
                binding.otpEditText1.text.toString() + binding.otpEditText2.text.toString() + binding.otpEditText3.text.toString() + binding.otpEditText4.text.toString() + binding.otpEditText5.text.toString() + binding.otpEditText6.text.toString()
            if (typedOTP.isNotEmpty() && typedOTP.length == 6) {
                val credential = PhoneAuthProvider.getCredential(OTP, typedOTP)
                binding.progressBar2.visibility = View.VISIBLE
                binding.buttonOtp.isEnabled = false
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(context, "Please enter valid OTP", Toast.LENGTH_SHORT).show()
            }
        }
        binding.textViewResendOtp.setOnClickListener {
            resendVerificationCode()
            resendOtpTextViewVisibility()
        }

        binding.textViewClearOtp.setOnClickListener {
            binding.otpEditText1.setText("")
            binding.otpEditText2.setText("")
            binding.otpEditText3.setText("")
            binding.otpEditText4.setText("")
            binding.otpEditText5.setText("")
            binding.otpEditText6.setText("")
            binding.otpEditText1.requestFocus()
        }


    }

    companion object
}