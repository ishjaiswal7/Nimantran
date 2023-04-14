package com.example.nimantran.ui.main.clientGuests

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nimantran.R
import com.example.nimantran.adapters.MyGuestListAdapter
import com.example.nimantran.databinding.FragmentMyGuestListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pub.devrel.easypermissions.EasyPermissions

class MyGuestListFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentMyGuestListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val myGuestListViewModel: MyGuestViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_guest_list, container, false)
        return binding.root
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun selectContact() {
        // Start an activity for the user to pick a phone number from contacts
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == Activity.RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            if (data != null) {
                data.data?.let { getContact(it) }
            } else {
                Log.e("TAG", "onActivityResult: data is null")
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage("Please select a contact")
                    .setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    @SuppressLint("Range")
    private fun getContact(uri: Uri) {
        // get the contact details from uri
        val SELECTION: String = "${ContactsContract.Data.LOOKUP_KEY} = ?"
        val SORT_ORDER = ContactsContract.Data.MIMETYPE
/*        val cursor = requireActivity().contentResolver.query(
            uri,
            PROJECTION,
            SELECTION,
            selectionArgs,
            SORT_ORDER
        )
        // result
        var name: String? = null
        var phone: String? = null
        var address: String? = null

        // upload to database
        myGuestListViewModel.saveGuest(
            db,
            name,
            phone,
            address,
            auth.currentUser?.uid.toString()
        )
 */
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myGuestListViewModel.getGuests(db,auth.currentUser?.uid) // fetch data only
        myGuestListViewModel.guests.observe(viewLifecycleOwner) { guests ->
            if (guests.isNotEmpty()) {
                binding.recyclerViewMyGuestList.adapter =
                    MyGuestListAdapter(requireActivity()) {
                        myGuestListViewModel.selectGuest(it)
                        val dir =
                            MyGuestListFragmentDirections.actionMyGuestListFragmentToEditGuestFragment(
                                it.id
                            )
                        findNavController().navigate(dir)
                    }

                (binding.recyclerViewMyGuestList.adapter as MyGuestListAdapter).submitList(
                    guests
                )
            } else {
                binding.recyclerViewMyGuestList.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutMyGuestList.isRefreshing) {
                binding.swipeRefreshLayoutMyGuestList.isRefreshing = false
            }
        }


        binding.fabMyGuestList.setOnClickListener {
            selectContact()
        }

/*
        myGuestListViewModel.guests.observe(viewLifecycleOwner) { guests ->
            if (guests.isNotEmpty()) {
                binding.recyclerViewMyGuestList.adapter =
                    MyGuestListAdapter(requireActivity()) {
                        myGuestListViewModel.selectGuest(it)
                    }

                (binding.recyclerViewMyGuestList.adapter as MyGuestListAdapter).submitList(
                    guests
                )
            } else {
                binding.recyclerViewMyGuestList.visibility = View.GONE
            }
            if (binding.swipeRefreshLayoutMyGuestList.isRefreshing) {
                binding.swipeRefreshLayoutMyGuestList.isRefreshing = false
            }
        }
 */

        binding.swipeRefreshLayoutMyGuestList.setOnRefreshListener {
            myGuestListViewModel.getGuests(db, auth.currentUser?.uid)
        }

        binding.cardViewAddGuest.setOnClickListener {
            val dir = MyGuestListFragmentDirections.actionMyGuestListFragmentToAddMyGuestFragment()
            findNavController().navigate(dir)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val COLL_MY_GUESTS = "myGuests"
        private const val SELECTION: String = "${ContactsContract.Data.LOOKUP_KEY} = ?"
        private const val SORT_ORDER = ContactsContract.Data.MIMETYPE
        const val REQUEST_SELECT_PHONE_NUMBER = 1
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        selectContact()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }
}