package com.example.nimantran.ui.authentication

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.admin.Client
import com.example.nimantran.ui.authentication.GetClientDetailsFragment.Companion.COLL_CLIENT
import com.example.nimantran.ui.main.settings.MyProfileFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ClientViewModel: ViewModel() {
    private val _client = MutableLiveData<Client>()
    val client: MutableLiveData<Client> = _client

    private val _selectedClient = MutableLiveData<Client?>()
    val selectedClient: MutableLiveData<Client?> = _selectedClient

    private val _isLoading = MutableLiveData(false)
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isSaved = MutableLiveData(false)
    val isSaved: MutableLiveData<Boolean> = _isSaved

    private val _isImgUploaded = MutableLiveData(false)
    val isImgUploaded: MutableLiveData<Boolean> = _isImgUploaded

    private val _downloadUri = MutableLiveData<Uri>()
    val downloadUri: MutableLiveData<Uri> = _downloadUri

    fun selectClient(client: Client) {
        _selectedClient.value = client
    }

    fun saveClient(
        context: Context,
        storage: FirebaseStorage,
        db: FirebaseFirestore,
        id: String,
        name: String,
        phone: String,
        gender: String,
        imageUri: Uri?,
    ) {
        _isLoading.value = true

        if (!validateClient(id,name, phone, gender, imageUri)) {
            _isLoading.value = false
            _isSaved.value = false
        } else {
            uploadToFirebase(context, storage, db, id, name, phone, gender, imageUri)
        }
    }

    private fun validateClient(
        id: String,
        name: String,
        phone: String,
        gender: String,
        imageUri: Uri?
    ): Boolean {
        return name.isNotEmpty() && gender.isNotEmpty() && id.isNotEmpty()
    }

    @SuppressLint("Range")
    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val fileName : String?
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        cursor?.close()
        return fileName
    }

    fun uploadToFirebase(
        context: Context,
        storage: FirebaseStorage,
        db: FirebaseFirestore,
        id: String,
        name: String,
        phone: String,
        gender: String,
        imageUri: Uri?
    ) {
        val storageRef = storage.reference
        imageUri?.let {uri ->
            val fileName = context.let { getFileNameFromUri(it, uri) }
            val fileRef = storageRef.child("clients/$fileName")
            val uploadTask = fileRef.putFile(imageUri)
            uploadTask.addOnFailureListener {
                _isImgUploaded.value = false
                it.message?.let {

                }
            }.addOnCompleteListener {task2->
                if (task2.isSuccessful) {
                    fileRef.downloadUrl.addOnSuccessListener {
                        _downloadUri.value = it
                        val client =
                            Client(
                                id,
                                name,
                                phone,
                                gender,
                                profileImg = downloadUri.value.toString()
                            )
                        db.collection(COLL_CLIENT).document(id).set(client).addOnSuccessListener {
                            _isLoading.value = false
                            _isSaved.value = true

                        }.addOnFailureListener {
                            _isLoading.value = false
                            _isSaved.value = false
                        }.addOnCanceledListener {
                            _isLoading.value = false
                            _isSaved.value = false
                        }
                    }
                    _isImgUploaded.value = true
                } else {
                    _isImgUploaded.value = false
                }
            }
        }
    }

    fun getClient(db: FirebaseFirestore, uid: String?){
        _isLoading.value = true
        db.collection(MyProfileFragment.COLL_CLIENT).document(uid!!).get().addOnSuccessListener {
            _isLoading.value = false
            _client.value = it.toObject(Client::class.java)
        }.addOnFailureListener {
            _isLoading.value = false

        }.addOnCanceledListener {
            _isLoading.value = false
        }
    }
}