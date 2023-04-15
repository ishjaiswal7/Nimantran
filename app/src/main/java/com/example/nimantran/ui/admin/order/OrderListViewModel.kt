package com.example.nimantran.ui.admin.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.user.MyOrder
import com.google.firebase.firestore.FirebaseFirestore

class OrderListViewModel : ViewModel() {
    private val _orders = MutableLiveData<List<MyOrder>>()
    val orders: LiveData<List<MyOrder>> = _orders

    private val _selectedOrder = MutableLiveData<MyOrder?>()
    val selectedOrder: MutableLiveData<MyOrder?> = _selectedOrder

    fun getOrders(db: FirebaseFirestore) {
        loadOrders(db)
        _selectedOrder.value = null
    }

    private fun loadOrders(db: FirebaseFirestore) {
        // fetch data from firebase firestore
        db.collection(OrderListFragment.COLL_ORDERS).get().addOnFailureListener {
            Log.e("OrderListViewModel", "Error fetching orders ${it.message}")
        }.addOnCanceledListener {
            Log.e("OrderListViewModel", "Cancelled fetching orders")
        }.addOnSuccessListener {
            val ordersLoaded = it.toObjects(MyOrder::class.java)
            _orders.value = ordersLoaded
            Log.d("OrderListViewModel", "Order loaded ${ordersLoaded.size}")
        }
    }

    fun selectOrder(order: MyOrder) {
        _selectedOrder.value = order
    }

    fun deselectOrder() {
        _selectedOrder.value = null
    }

}