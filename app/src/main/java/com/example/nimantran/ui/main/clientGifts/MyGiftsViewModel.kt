package com.example.nimantran.ui.main.clientGifts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.admin.Client
import com.example.nimantran.models.admin.Gift
import com.example.nimantran.models.user.MyOrder
import com.example.nimantran.ui.main.clientGifts.MyGiftsFragment.Companion.COLL_MY_GIFTS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val s = "myOrders"

class MyGiftsViewModel : ViewModel() {
    private val _myGifts = MutableLiveData<List<Gift>>()
    val myGifts: LiveData<List<Gift>> = _myGifts

    private val _selectedMyGift = MutableLiveData<Gift?>()
    val selectedMyGift: MutableLiveData<Gift?> = _selectedMyGift

    private val _me = MutableLiveData<Client>()
    val me: MutableLiveData<Client> = _me

    private val _giftForMe = true
    var giftForMe: Boolean = _giftForMe

    private val _orderId = MutableLiveData<String>()
    val orderId: LiveData<String> = _orderId

    var userGiftQty = 1
    var userAddress = ""
    fun getUserDetails(db: FirebaseFirestore, auth: FirebaseAuth) {
        db.collection("clients").document(auth.currentUser!!.uid).get().addOnSuccessListener {
            _me.value = it.toObject(Client::class.java)
            Log.e("MyGiftsViewModel", "User details loaded ${it.toObject(Client::class.java)}")
        }.addOnFailureListener {
            Log.e("MyGiftsViewModel", "Error fetching user details ${it.message}")
        }
    }

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
        db.collection(COLL_ORDERS).add(order).addOnSuccessListener {
            Log.d("MyGiftsViewModel", "Order added")
            _orderId.value = it.id
        }.addOnFailureListener {
            Log.e("MyGiftsViewModel", "Error adding order ${it.message}")
        }
    }

    companion object {
        const val COLL_MY_GIFTS = "gifts"
        const val COLL_ORDERS = "myOrders"
    }
}