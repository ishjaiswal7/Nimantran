package com.example.nimantran.ui.main.addcard

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nimantran.R
import com.example.nimantran.SmsSentReceiver
import com.example.nimantran.databinding.FragmentGetCardMsgBinding
import com.example.nimantran.ui.main.clientGuests.MyGuestViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import pub.devrel.easypermissions.EasyPermissions


class GetCardMsgFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentGetCardMsgBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private val templateCardViewModel: TemplateCardViewModel by activityViewModels()
    private val guestViewModel: MyGuestViewModel by activityViewModels()
    var picker: DatePickerDialog? = null

    val PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.SEND_SMS,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGetCardMsgBinding.inflate(inflater, container, false)
        binding.viewModel = templateCardViewModel
        binding.viewModel2 = guestViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if (EasyPermissions.hasPermissions(requireContext(), *PERMISSIONS)) {
            updateUI()
        } else {
            requestPermission()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun requestPermission() {
        EasyPermissions.requestPermissions(
            this,
            "Please grant the permissions to send invitation",
            123,
            *PERMISSIONS
        )
    }

    private fun updateUI() {
        Toast.makeText(requireContext(), "updated ui", Toast.LENGTH_SHORT).show()
        binding.TextViewEditDate.setOnClickListener {
            val cal = java.util.Calendar.getInstance()
            val year = cal.get(java.util.Calendar.YEAR)
            val month = cal.get(java.util.Calendar.MONTH)
            val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
            picker = DatePickerDialog(requireContext(), { view, year, monthOfYear, dayOfMonth ->
                binding.TextViewEditDate.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
            }, year, month, day)
            picker!!.show()
        }
        binding.buttonSendInvitation.setOnClickListener {
            it.setBackgroundColor(resources.getColor(R.color.purple_200))
            val name = binding.TextViewEditName.text.toString()
            val phone = binding.TextViewEditPhone.text.toString()
            val date = binding.TextViewEditDate.text.toString()
            val title = binding.textViewCardTitle.text.toString()
            val message = binding.TextViewEditMessage.text.toString()
            templateCardViewModel.sendInvitation(
                requireActivity(),
                storage,
                db,
                auth.currentUser?.uid.toString(),
                name,
                phone,
                date,
                title,
                message,
                guestViewModel.selectedGuest.value?.toList() ?: listOf(),
                templateCardViewModel.invitationCard.value.toString()
            )
            AlertDialog.Builder(requireContext()).setTitle("Invitation Sent")
                .setMessage("Invitation has been sent to the guest")
                .setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }.show()
        }
        this.templateCardViewModel.inviteId.observe(viewLifecycleOwner) {
            if (it != null) {
                for (guest in templateCardViewModel.inviteList.value!!) {
                    sendInviteCodeAsSms(guest.phone, it)
                }
            }
        }
    }

    private fun sendInviteCodeAsSms(phone: String, it: String) {
        //send sms
        // version check
        val smsManager: SmsManager
        var sentIntent: PendingIntent
        if (Build.VERSION.SDK_INT >= 23) {
            //if SDK is greater that or equal to 23 then
            //this is how we will initialize the SmsManager
            smsManager = requireActivity().getSystemService(SmsManager::class.java)
        } else {
            //if user's SDK is less than 23 then
            //SmsManager will be initialized like this
            smsManager = SmsManager.getDefault()
        }
        if (Build.VERSION.SDK_INT < 31) {
            sentIntent = PendingIntent.getBroadcast(
                requireContext(),
                0,
                Intent(requireContext(), SmsSentReceiver::class.java),
                0
            )
        } else {
            sentIntent = PendingIntent.getBroadcast(
                requireContext(),
                0,
                Intent(requireContext(), SmsSentReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        }
        smsManager.sendTextMessage(
            phone,
            null,
            "${binding.TextViewEditMessage.text}\nYour invitation code is $it",
            sentIntent,
            null
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    companion object {}

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //on permission granted
        updateUI()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //on permission denied
    }
}