package com.example.nimantran.ui.main.settings

import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.admin.Client
import com.example.nimantran.ui.main.settings.MyProfileFragment.Companion.COLL_CLIENT
import com.google.firebase.firestore.FirebaseFirestore

class MyProfileViewModel : ViewModel(){
    private val _client = MutableLiveData<Client>()
    val client: LiveData<Client> = _client

    private val _selectedClient = MutableLiveData<Client>()
    val selectedClient: LiveData<Client> = _selectedClient

    private val _isLoading = MutableLiveData(false)
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isSaved = MutableLiveData(false)
    val isSaved: MutableLiveData<Boolean> = _isSaved

    private val _isImgUploaded = MutableLiveData(false)
    val isImgUploaded: MutableLiveData<Boolean> = _isImgUploaded

    private val _downloadUri = MutableLiveData<Uri>()
    val downloadUri: MutableLiveData<Uri> = _downloadUri

    fun getClient(db: FirebaseFirestore, uid: String?){  
        _isLoading.value = true
        db.collection(COLL_CLIENT).document(uid!!).get().addOnSuccessListener {
            _isLoading.value = false
            _client.value = it.toObject(Client::class.java)
        }.addOnFailureListener {
            _isLoading.value = false
        }.addOnCanceledListener {
            _isLoading.value = false
        }
    }

    fun updateClient(
        db: FirebaseFirestore,
        name: String,
        gender: String,
    ){
        val clientt = client.value
        db.collection(COLL_CLIENT)
            .whereEqualTo("id", clientt?.id).get().addOnFailureListener{
                Log.e("MyProfileViewModel", "Error updating client ${it.message}")
            }.addOnCanceledListener {
                Log.e("MyProfileViewModel", "Cancelled updating client")
            }.addOnSuccessListener {

                try {
                    db.collection(COLL_CLIENT)
                        .document(it.documents[0].id).update(
                            mapOf(
                                "name" to name,
                                "gender" to gender
                            )
                        ).addOnCanceledListener {
                            Log.e("MyProfileViewModel", "Cancelled updating client")
                        }.addOnFailureListener {
                            Log.e("MyProfileViewModel", "Error updating client ${it.message}")
                        }.addOnSuccessListener {
                            Log.e("MyProfileViewModel", "Client updated")
//                            _isSaved.value = true
                        }
                } catch (e: Exception) {
                    Log.e("MyProfileViewModel", "Error updating client ${e.message}")
                }
            }
    }

    fun resetStatus(){
        _isSaved.value = false
        _isImgUploaded.value = false
    }
}