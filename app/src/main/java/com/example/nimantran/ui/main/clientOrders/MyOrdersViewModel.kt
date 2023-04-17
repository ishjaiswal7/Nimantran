package com.example.nimantran.ui.main.clientOrders

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.user.MyOrder
import com.example.nimantran.ui.main.clientOrders.MyOrdersFragment.Companion.COLL_ORDERS
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MyOrdersViewModel : ViewModel(){
    private val _myOrders = MutableLiveData<List<MyOrder>>()
    val myOrders: MutableLiveData<List<MyOrder>> = _myOrders

    private val _selectedOrder = MutableLiveData<MyOrder>()
    val selectedOrder: MutableLiveData<MyOrder> = _selectedOrder


    fun getMyOrders(db: FirebaseFirestore, uid: String?){
        loadMyOrders(db, uid ?: "")
        unselectOrder()
    }

    private fun loadMyOrders(db: FirebaseFirestore, uid: String = ""){
        // fetch data from firebase firestore
        db.collection(COLL_ORDERS)
            .whereEqualTo("clientID", uid).get().addOnFailureListener {
            Log.e("MyOrdersViewModel", "Error fetching myorders ${it.message}")
        }.addOnCanceledListener {
            Log.e("MyOrdersViewModel", "Cancelled fetching myorders")
        }.addOnSuccessListener {
            val myordersLoaded = it.toObjects(MyOrder::class.java)
            _myOrders.value = myordersLoaded
            Log.d("MyOrdersViewModel", "MyOrders loaded ${myordersLoaded.size}")
        }
    }

    fun updateMyOrder(myorder: MyOrder, isSelected: Boolean){
        if (isSelected) selectMyOrder(myorder)
        else unselectOrder()
    }

    fun selectMyOrder(myorder: MyOrder){
        _selectedOrder.value = myorder

        Log.e("TAG", "Selected myorder ${myorder.id}")
    }
    fun unselectOrder(){
        if (_selectedOrder.value != null) {
            _selectedOrder.value = MyOrder()
            Log.e("TAG", "Deselect myorder")
        }
    }


}