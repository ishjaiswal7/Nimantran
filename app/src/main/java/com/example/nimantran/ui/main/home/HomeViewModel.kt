package com.example.nimantran.ui.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.user.MyCards
import com.example.nimantran.ui.main.home.HomeFragment.Companion.COLL_MYCARDS
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel: ViewModel() {
    private val _mycards = MutableLiveData<List<MyCards>>()
    val mycards: LiveData<List<MyCards>> = _mycards

    fun getMyCards(db: FirebaseFirestore){
        loadMyCards(db)
    }

    private fun loadMyCards(db: FirebaseFirestore) {
        // fetch data from firebase firestore
        db.collection(COLL_MYCARDS).get().addOnFailureListener {
             Log.e("HomeViewModel", "Error fetching mycards ${it.message}")
        }.addOnCanceledListener {
             Log.e("HomeViewModel", "Cancelled fetching mycards")
        }.addOnSuccessListener {
            val mycardsLoaded = it.toObjects(MyCards::class.java)
            _mycards.value = mycardsLoaded
             Log.d("HomeViewModel", "MyCards loaded ${mycardsLoaded.size}")
        }

    }

}