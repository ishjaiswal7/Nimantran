package com.example.nimantran.ui.admin.insight

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.admin.Client
import com.google.firebase.firestore.FirebaseFirestore

class AppInsightViewModel: ViewModel(){
    private val _clients = MutableLiveData<List<Client>>()
    val clients: MutableLiveData<List<Client>> = _clients

    fun getClients(db: FirebaseFirestore) {
        loadClients(db)
    }

    private fun loadClients(db: FirebaseFirestore) {
        db.collection(AppInsightFragment.COLL_CLIENTS).get().addOnFailureListener {
            Log.e("UserListViewModel", "Error fetching clients ${it.message}")
        }.addOnCanceledListener {
            Log.e("UserListViewModel", "Cancelled fetching clients")
        }.addOnSuccessListener {
            val clientsLoaded = it.toObjects(Client::class.java)
            _clients.value = clientsLoaded
            Log.d("UserListViewModel", "Clients loaded ${clientsLoaded.size}")
        }
    }

    fun totalClients(): Int {
        return clients.value?.size ?: 0
    }
}