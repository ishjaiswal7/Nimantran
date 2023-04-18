package com.example.nimantran.ui.main.addcard

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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


class getCardMsgFragment : Fragment(), EasyPermissions.PermissionCallbacks {
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
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.RECEIVE_SMS
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!EasyPermissions.hasPermissions(requireContext(), *PERMISSIONS)) {
            EasyPermissions.requestPermissions(
                this,
                "Please grant the permissions to send invitation",
                123,
                *PERMISSIONS
            )
        } else {
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
//                    shareToAllGuests()
//                    shareToAllGuestsUsingSms()

                        dialog.dismiss()
                    }.show()
            }
            this.templateCardViewModel.isSaved.observe(viewLifecycleOwner) {
                if (it) {
//                shareToAllGuests()
                    sendImageAndLinkViaSms(
                        binding.TextViewEditPhone.text.toString(),
                        "Invitation Card",
                        templateCardViewModel.invitationCard.value.toString()
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun shareToAllGuests() {
        templateCardViewModel.inviteList.value?.forEach() {
            // send to whatsapp
            val whatsappIntent =
                requireActivity().packageManager.getLaunchIntentForPackage("com.whatsapp")

            if (whatsappIntent != null) {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Invitation Card")
                sendIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    Uri.parse(templateCardViewModel.invitationCard.value.toString())
                )
                sendIntent.type = "image/*"
                sendIntent.setPackage("com.whatsapp")
                startActivity(sendIntent)
            } else {
                Toast.makeText(requireContext(), "Whatsapp not installed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun shareToAllGuestsUsingSms() {
        templateCardViewModel.inviteList.value?.forEach() {
            val smsManager =
                SmsManager.getSmsManagerForSubscriptionId(SmsManager.getDefaultSmsSubscriptionId())

            val message =
                "${binding.TextViewEditName.text} has invited you to attend the event ${binding.textViewCardTitle.text} on ${binding.TextViewEditDate.text}}"
            smsManager.sendTextMessage(
                it.phone, null, "Invitation Card", null, null
            )
        }
    }

    fun sendImageAndLinkViaSms(phoneNumber: String, message: String, imageUrl: String) {
        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requireContext().getSystemService(SmsManager::class.java)
                .createForSubscriptionId(SubscriptionManager.getDefaultSmsSubscriptionId())
        } else {
            SmsManager.getSmsManagerForSubscriptionId(SmsManager.getDefaultSmsSubscriptionId())
        }
        val parts = smsManager.divideMessage(message)
        smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
        smsManager.sendMultimediaMessage(
            requireContext(), Uri.parse(imageUrl), phoneNumber, null, null
        )
    }

    companion object {}

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }
}