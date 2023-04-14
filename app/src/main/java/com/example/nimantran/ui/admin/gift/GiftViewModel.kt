package com.example.nimantran.ui.admin.gift

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.admin.Gift
import com.example.nimantran.ui.admin.gift.GiftListFragment.Companion.COLL_GIFTS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage


class GiftViewModel : ViewModel() {
    private val _gifts = MutableLiveData<List<Gift>>()
    val gifts: LiveData<List<Gift>> = _gifts

    private val _selectedGift = MutableLiveData<Gift?>()
    val selectedGift: LiveData<Gift?> = _selectedGift

    private val _isLoading = MutableLiveData(false)
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _isSaved = MutableLiveData(false)
    val isSaved: MutableLiveData<Boolean> = _isSaved

    private val _isImgUploaded = MutableLiveData(false)
    val isImgUploaded: MutableLiveData<Boolean> = _isImgUploaded

    private val _downloadUri = MutableLiveData<Uri>()
    val downloadUri: MutableLiveData<Uri> = _downloadUri


    fun getGifts(db: FirebaseFirestore) {
        loadGifts(db)
        resetStatus()
    }
    private fun loadGifts(db: FirebaseFirestore) {
        // fetch data from firebase firestore
        db.collection(COLL_GIFTS)
            .orderBy("price", Query.Direction.ASCENDING)
            .get().addOnFailureListener {
                Log.e("GiftViewModel", "Error fetching gifts ${it.message}")
            }.addOnCanceledListener {
                Log.e("GiftViewModel", "Cancelled fetching gifts")
            }.addOnSuccessListener {
                val giftLoaded = it.toObjects(Gift::class.java)
                _gifts.value = giftLoaded
                Log.d("GiftViewModel", "Gifts loaded ${giftLoaded.size}")
            }
    }

    fun selectGift(gift: Gift) {
        _selectedGift.value = gift
    }

    fun deselectGift() {
        _selectedGift.value = null
    }
    fun saveGift(
        context: Context,
        storage: FirebaseStorage,
        db: FirebaseFirestore,
        item: String,
        price: String,
        quantity: String,
        description: String,
        imageUri: Uri?,
    ) {
        _isLoading.value = true

        if (!validateGift(item, quantity, description, price, imageUri)) {
            _isLoading.value = false
            _isSaved.value = false
        } else {
            uploadToFirebase(context, storage, imageUri, item, price, quantity, description, db)
        }
    }

    private fun validateGift(
        item: String,
        quantity: String,
        description: String,
        price: String,
        imageUri: Uri?
    ): Boolean {
        return item.isNotEmpty() && quantity.isNotEmpty() && description.isNotEmpty() && price.isNotEmpty()
    }



    private fun resetStatus() {
        _isSaved.value = false
    }

    @SuppressLint("Range")
    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val fileName: String?
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        cursor?.close()
        return fileName
    }

    fun uploadToFirebase(
        context: Context,
        storage: FirebaseStorage,
        imageUri: Uri?,
        item: String,
        price: String,
        quantity: String,
        description: String,
        db: FirebaseFirestore
    ) {
        val storageRef = storage.reference
        imageUri?.let { uri ->
            val filename = context.let { getFileNameFromUri(it, uri) }
            val fileRef = storageRef.child("gifts/" + filename.toString())
            val uploadTask = fileRef.putFile(imageUri)
            uploadTask.addOnFailureListener {
                _isImgUploaded.value = false
                it.message?.let {

                }
            }.addOnCompleteListener { task2 ->
                if (task2.isSuccessful) {
                    fileRef.downloadUrl.addOnSuccessListener {
                        _downloadUri.value = it
                        val gift =
                            Gift(
                                item,
                                description,
                                quantity = quantity.toInt(),
                                price = price.toDouble(),
                                image = downloadUri.value.toString()
                            )
                        db.collection(COLL_GIFTS).add(gift).addOnSuccessListener {
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

    fun deleteGift(db: FirebaseFirestore) {
        Log.d("GiftViewModel", "Deleting gift ${selectedGift.value?.item}")
        db.collection(COLL_GIFTS)
            .whereEqualTo("item", selectedGift.value?.item).get().addOnFailureListener {
                Log.e("GiftViewModel", "Error deleting gift ${it.message}")
                getGifts(db)
            }.addOnCanceledListener {
                Log.e("GiftViewModel", "Cancelled deleting gift")
            }.addOnSuccessListener {
                try {
                    db.collection(COLL_GIFTS)
                        .document(it.documents[0].id).delete().addOnCanceledListener {
                            Log.e("GiftViewModel", "Cancelled deleting gift")
                        }.addOnFailureListener {
                            Log.e("GiftViewModel", "Error deleting gift ${it.message}")
                        }.addOnSuccessListener {
                            Log.d("GiftViewModel", "Gift deleted ${selectedGift.value?.item}")
                        }
                } catch (e: Exception) {
                    Log.e("GiftViewModel", "Error deleting gift ${e.message}")
                }
            }
    }

    fun updateGift(
        db: FirebaseFirestore,
        item: String,
        price: String,
        quantity: String,
        description: String,
    ){
/*
        _isLoading.value = true

        if (!validateGiftUpdate(quantity, description, price)) {
            _isLoading.value = false
            _isSaved.value = false
        } else {
            updateToFirebase(db, item, price, quantity, description)
        }

    }

    private fun validateGiftUpdate(
        quantity: String,
        description: String,
        price: String
    ): Boolean {
        return quantity.isNotEmpty() && description.isNotEmpty() && price.isNotEmpty()
    }
    fun updateToFirebase(
        db: FirebaseFirestore,
        item: String,
        price: String,
        quantity: String,
        description: String,
    )  {

 */
        val gift = selectedGift.value
        db.collection(COLL_GIFTS)
            .whereEqualTo("item", gift?.item).get().addOnFailureListener {
                Log.e("GiftViewModel", "Error updating gift ${it.message}")
                getGifts(db)
            }.addOnCanceledListener {
                Log.e("GiftViewModel", "Cancelled updating gift")
            }.addOnSuccessListener {

                try {
                    db.collection(COLL_GIFTS)
                        .document(it.documents[0].id).update(
                            mapOf(
                                "item" to item,
                                "price" to price.toDouble(),
                                "quantity" to quantity.toInt(),
                                "description" to description
                            )
                        ).addOnCanceledListener {
                            Log.e("GiftViewModel", "Cancelled updating gift")
                        }.addOnFailureListener {
                            Log.e("GiftViewModel", "Error updating gift ${it.message}")
                        }.addOnSuccessListener {
                            Log.d("GiftViewModel", "Gift updated ${selectedGift.value?.item}")
//                            _isSaved.value = true
                        }
                } catch (e: Exception) {
                    Log.e("GiftViewModel", "Error updating gift ${e.message}")
                }
            }
    }
}
