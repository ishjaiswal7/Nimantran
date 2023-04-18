package com.example.nimantran.ui.admin.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.admin.Client
import com.example.nimantran.models.user.Guest
import com.example.nimantran.models.user.MyOrder
import com.google.firebase.firestore.FirebaseFirestore

class OrderListViewModel : ViewModel() {
    private val _orders = MutableLiveData<List<MyOrder>>()
    val orders: LiveData<List<MyOrder>> = _orders

    private val _guests= MutableLiveData<List<Guest>>()
    val guests: MutableLiveData<List<Guest>> = _guests

    private val _client= MutableLiveData<Client>()
    val client: MutableLiveData<Client> = _client

    private val _selectedOrder = MutableLiveData<MyOrder?>()
    val selectedOrder: MutableLiveData<MyOrder?> = _selectedOrder

    private val _isLoading = MutableLiveData(false)
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isSaved = MutableLiveData(false)
    val isSaved: MutableLiveData<Boolean> = _isSaved

    var selectedOrderClientName: String = ""
    var selectedOrderClientPhone: String = ""

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

    fun getGuests() {
        //fetch guest of selected order
        if (selectedOrder != null) {
            _guests.value = _selectedOrder.value?.guest
        }
    }

    fun getClient(db: FirebaseFirestore) {
        db.collection(OrderStatusFragment.COLL_CLIENTS)
            .whereEqualTo("id", selectedOrder.value?.clientID.toString())
            .get().addOnFailureListener {
            Log.e("OrderListViewModel", "Error fetching client ${it.message}")
        }.addOnCanceledListener {
            Log.e("OrderListViewModel", "Cancelled fetching client")

        }.addOnSuccessListener {
            val clientLoaded = it.toObjects(Client::class.java)[0]
            Log.d("OrderListViewModel", "Client loaded ${clientLoaded.name}")
                selectedOrderClientName = clientLoaded.name
                selectedOrderClientPhone = clientLoaded.phone.toString()
        }
    }

    fun updateOrderStatus(db: FirebaseFirestore, status: String) {
        val order = selectedOrder.value
        db.collection(OrderStatusFragment.COLL_ORDERS)
            .whereEqualTo("id", order?.id).get().addOnFailureListener{
                Log.e("OrderListViewModel", "Error fetching order ${it.message}")
            }.addOnCanceledListener {
                Log.e("OrderListViewModel", "Cancelled fetching order")
            }.addOnSuccessListener {
                try {
                    db.collection(OrderStatusFragment.COLL_ORDERS)
                        .document(it.documents[0].id)
                        .update(
                            mapOf(
                                "orderStatus" to status
                            )
                        )
                        .addOnCanceledListener {
                            Log.e("OrderListViewModel", "Cancelled updating order status")
                        }.addOnFailureListener {
                            Log.e("OrderListViewModel", "Error updating order status ${it.message}")
                        }.addOnSuccessListener {
                            Log.d("OrderListViewModel", "Order status updated to $status")
                        }
                }catch (e:Exception) {
                    Log.e("OrderListViewModel", "Error updating order status ${e.message}")
                }
            }
    }


}