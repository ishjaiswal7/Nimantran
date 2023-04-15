package com.example.nimantran.ui.main.clientGifts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.admin.Gift
import com.example.nimantran.models.user.MyOrder
import com.example.nimantran.ui.main.clientGifts.MyGiftsFragment.Companion.COLL_MY_GIFTS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyGiftsViewModel : ViewModel() {
    private val _myGifts = MutableLiveData<List<Gift>>()
    val myGifts: LiveData<List<Gift>> = _myGifts

    private val _selectedMyGift = MutableLiveData<Gift?>()
    val selectedMyGift: MutableLiveData<Gift?> = _selectedMyGift

    private val _giftForMe = true
    var giftForMe: Boolean = _giftForMe

    var userGiftQty = 1
    var userAddress = ""

    fun getMyGifts(db: FirebaseFirestore) {
        loadMyGifts(db)
        deselectGift()
    }

    private fun loadMyGifts(db: FirebaseFirestore) {
        // fetch data from firebase firestore
        db.collection(COLL_MY_GIFTS).get().addOnFailureListener {
            Log.e("MyGiftsViewModel", "Error fetching gifts ${it.message}")
        }.addOnCanceledListener {
            Log.e("MyGiftsViewModel", "Cancelled fetching gifts")
        }.addOnSuccessListener {
            val giftLoaded = it.toObjects(Gift::class.java)
            _myGifts.value = giftLoaded
            Log.d("MyGiftsViewModel", "Gifts loaded ${giftLoaded.size}")
        }
    }

    fun sendToMe() {
        // send gift to me
        giftForMe = true
    }

    fun sendToGuest() {
        // send gift to guest
        giftForMe = false
    }

    fun selectMyGift(gift: Gift) {
        _selectedMyGift.value = gift
    }

    fun deselectGift() {
        _selectedMyGift.value = null
    }

    fun addOrder(db: FirebaseFirestore, auth: FirebaseAuth, order: MyOrder) {
        db.collection("myOrders").add(order).addOnSuccessListener {
            Log.d("MyGiftsViewModel", "Order added")

        }.addOnFailureListener {
            Log.e("MyGiftsViewModel", "Error adding order ${it.message}")
        }
    }
}