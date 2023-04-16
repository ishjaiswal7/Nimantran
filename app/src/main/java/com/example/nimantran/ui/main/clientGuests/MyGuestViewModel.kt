package com.example.nimantran.ui.main.clientGuests

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.user.Guest
import com.example.nimantran.models.user.Invite
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

    private val _message = MutableLiveData<String>()
    val message: MutableLiveData<String> = _message

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

    fun inviteGuest(
        db: FirebaseFirestore,
        uid: String,
    ) {
        _isLoading.value = true
        // get selected guests
        val selectedGuests = _selectedGuest.value
        if (selectedGuests != null) {
            // save guests to invites collection
            for (guest in selectedGuests) {
                val invite = Invite(
                    guestId = guest.id,
                    guestName = guest.name,
                    phone = guest.phone,
                    response = "Not Responded",
                    //message = _message.value ?: "",
                    //date = Calendar.getInstance().time.toString()
                )
                db.collection(COLL_MY_INVITES).add(invite).addOnSuccessListener {
                    _isLoading.value = false
                    _isSaved.value = true
                    Log.d("MyGuestViewModel", "Guest invited")
                }.addOnFailureListener {
                    _isLoading.value = false
                    _isSaved.value = false
                    Log.e("MyGuestViewModel", "Error inviting guest ${it.message}")
                }.addOnCanceledListener {
                    _isLoading.value = false
                    _isSaved.value = false
                    Log.e("MyGuestViewModel", "Cancelled inviting guest")
                }
            }
        } else {
            _isLoading.value = false
            _isSaved.value = false
            Log.e("MyGuestViewModel", "No guest selected")
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

    fun updateInvitedGuest(guest: Guest, isSelected: Boolean) {
        if (isSelected) selectGuest(guest)
        else unSelectGuest(guest)
    }

    companion object {
        const val COLL_MY_INVITES = "invites"
    }
}