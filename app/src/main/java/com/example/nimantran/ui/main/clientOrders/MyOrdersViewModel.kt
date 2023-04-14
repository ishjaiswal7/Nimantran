package com.example.nimantran.ui.main.clientOrders

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.user.MyOrder
import com.example.nimantran.ui.main.clientOrders.MyOrdersFragment.Companion.COLL_ORDERS
import com.google.firebase.firestore.FirebaseFirestore

class MyOrdersViewModel : ViewModel(){
    private val _myorders = MutableLiveData<List<MyOrder>>()
    val myorders: MutableLiveData<List<MyOrder>> = _myorders

    private val _selectedOrder = MutableLiveData<MyOrder>()
    val selectedOrder: MutableLiveData<MyOrder> = _selectedOrder



    fun getMyOrders(db: FirebaseFirestore){
        loadMyOrders(db)
        deselectOrder()
    }

    private fun loadMyOrders(db: FirebaseFirestore){
        // fetch data from firebase firestore
        db.collection(COLL_ORDERS).get().addOnFailureListener {
            Log.e("MyOrdersViewModel", "Error fetching myorders ${it.message}")
        }.addOnCanceledListener {
            Log.e("MyOrdersViewModel", "Cancelled fetching myorders")
        }.addOnSuccessListener {
            val myordersLoaded = it.toObjects(MyOrder::class.java)
            _myorders.value = myordersLoaded
            Log.d("MyOrdersViewModel", "MyOrders loaded ${myordersLoaded.size}")
        }
    }

    fun selectOrder(myorder: MyOrder){
        _selectedOrder.value = myorder
    }

    fun deselectOrder() {
  //      _selectedOrder.value = null
    }


}