package com.example.nimantran.ui.admin.client

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.admin.Client
import com.google.firebase.firestore.FirebaseFirestore

class ClientListViewModel : ViewModel() {
    private val _clients = MutableLiveData<List<Client>>()
    val clients: MutableLiveData<List<Client>> = _clients

    private val _selectedClient = MutableLiveData<Client?>()
    val selectedClient: MutableLiveData<Client?> = _selectedClient

    fun getClients(db: FirebaseFirestore) {
        loadClients(db)
        deselectClient()
    }

    private fun loadClients(db: FirebaseFirestore) {
       db.collection(ClientListFragment.COLL_CLIENTS).get().addOnFailureListener {
            Log.e("UserListViewModel", "Error fetching clients ${it.message}")
       }.addOnCanceledListener {
            Log.e("UserListViewModel", "Cancelled fetching clients")
       }.addOnSuccessListener {
            val clientsLoaded = it.toObjects(Client::class.java)
            _clients.value = clientsLoaded
            Log.d("UserListViewModel", "Clients loaded ${clientsLoaded.size}")
       }
    }

    fun selectClient(client: Client){
        _selectedClient.value = client
    }

    fun deselectClient(){
        _selectedClient.value = null
    }

    fun deleteClient(db: FirebaseFirestore, client: Client){
        db.collection(ClientListFragment.COLL_CLIENTS).document(client.id)
            .delete().addOnFailureListener {
                Log.e("UserListViewModel", "Error deleting client ${it.message}")
            }.addOnCanceledListener {
                Log.e("UserListViewModel", "Cancelled deleting client")
            }.addOnSuccessListener {
                Log.d("UserListViewModel", "Client deleted")
            }
    }
}