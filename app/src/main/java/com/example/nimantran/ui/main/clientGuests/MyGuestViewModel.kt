package com.example.nimantran.ui.main.clientGuests

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.user.Guest
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MyGuestViewModel : ViewModel() {
    private val _guests = MutableLiveData<List<Guest>>()
    val guests: MutableLiveData<List<Guest>> = _guests

    private val _selectedGuest = MutableLiveData<Set<Guest>>()
    val selectedGuest: MutableLiveData<Set<Guest>> = _selectedGuest

    private val _isLoading = MutableLiveData(false)
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isSaved = MutableLiveData(false)
    val isSaved: MutableLiveData<Boolean> = _isSaved

    fun getGuests(db: FirebaseFirestore, uid: String?) {
        loadGuests(db, uid ?: "")
        clearGuestList()
    }

    private fun loadGuests(db: FirebaseFirestore, uid: String = "") {
        // fetch data from firebase firestore
        db.collection(MyGuestListFragment.COLL_MY_GUESTS)
            .whereEqualTo("clientId", uid).get().addOnFailureListener {
                Log.e("MyGuestListViewModel", "Error fetching guests ${it.message}")
            }.addOnCanceledListener {
                Log.e("MyGuestListViewModel", "Cancelled fetching guests")
            }.addOnSuccessListener {
                val guestsLoaded = it.toObjects(Guest::class.java)
                _guests.value = guestsLoaded
                Log.d("MyGuestListViewModel", "Guest loaded ${guestsLoaded.size}")
            }
    }

    fun updateGuest(guest: Guest, isSelected: Boolean) {
        if (isSelected) selectGuest(guest)
        else unSelectGuest(guest)
    }

    fun selectGuest(guest: Guest) {
        if (_selectedGuest.value == null) {
            _selectedGuest.value = setOf(guest)
        } else {
            _selectedGuest.value = _selectedGuest.value?.plus(guest)
        }
        Log.e("TAG", "Selected guest ${guest.name}")
    }

    fun unSelectGuest(guest: Guest) {
        if (_selectedGuest.value != null) {
            _selectedGuest.value = _selectedGuest.value?.minus(guest)
            Log.e("TAG", "Deselected guest ${guest.name}")
        }
    }

    fun clearGuestList() {
        if (_selectedGuest.value != null) {
            _selectedGuest.value = setOf()
            Log.e("TAG", "Deselected guest")
        }
    }

    fun saveGuest(
        db: FirebaseFirestore,
        name: String,
        phone: String,
        address: String,
        id: String,
        clientId: String
    ) {
        _isLoading.value = true

        if (!validateGuest(name, phone, address, id)) {
            _isLoading.value = false
            _isSaved.value = false
            Log.e("MyGuestViewModel", "Invalid guest")
        } else {
            val guest = Guest(name, phone, address, id, clientId)
            db.collection(AddMyGuestFragment.COLL_MY_GUESTS).add(guest).addOnSuccessListener {
                _isLoading.value = false
                _isSaved.value = true
                Log.d("MyGuestViewModel", "Guest saved")
            }.addOnFailureListener {
                _isLoading.value = false
                _isSaved.value = false
                Log.e("MyGuestViewModel", "Error saving guest ${it.message}")
            }.addOnCanceledListener {
                _isLoading.value = false
                _isSaved.value = false
                Log.e("MyGuestViewModel", "Cancelled saving guest")
            }
        }
    }

    private fun validateGuest(
        name: String,
        phone: String,
        address: String,
        id: String
    ): Boolean {
        return name.isNotEmpty() && phone.isNotEmpty() && id.isNotEmpty()
    }

    fun resetSaveStatus() {
        _isSaved.value = false
    }

    fun getUid(): String {
        return UUID.randomUUID().toString()
    }

/*
    val contacts = Contacts(context)
        .query()
        .where {
            (Name.GivenName startsWith "leo") and
                    (ContactsContract.CommonDataKinds.Email.Address { endsWith("gmail.com") or endsWith("hotmail.com") }) and
                    (Address.Country equalToIgnoreCase "us") and
                    (Event { (Date lessThan Date().toWhereString()) and (Type equalTo EventEntity.Type.BIRTHDAY) }) and
                    (Contact.Options.Starred equalTo true) and
                    (ContactsContract.CommonDataKinds.Nickname.Name equalTo "DarEdEvil") and
                    (ContactsContract.CommonDataKinds.Organization.Company `in` listOf("facebook", "FB")) and
                    (ContactsContract.CommonDataKinds.Note.Note.isNotNullOrEmpty())
        }
        .accounts(
            Account("john.doe@gmail.com", "com.google"),
            Account("john.doe@myspace.com", "com.myspace"),
        )
        .include { setOf(
            Contact.Id,
            Contact.DisplayNamePrimary,
            ContactsContract.CommonDataKinds.Phone.Number
        ) }
        .orderBy(ContactsFields.DisplayNamePrimary.desc())
        .offset(0)
        .limit(5)
        .find()

  */


}