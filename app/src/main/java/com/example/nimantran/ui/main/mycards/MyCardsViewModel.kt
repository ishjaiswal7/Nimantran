package com.example.nimantran.ui.main.mycards

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.user.Invite
import com.example.nimantran.models.user.MyCards
import com.google.firebase.firestore.FirebaseFirestore

class MyCardsViewModel :ViewModel(){
    private val _myCards = MutableLiveData<List<MyCards>>()
    val myCards: MutableLiveData<List<MyCards>> = _myCards

    private val _invite = MutableLiveData<List<Invite>>()
    val invite: MutableLiveData<List<Invite>> = _invite

    private val _selectedMyCard = MutableLiveData<MyCards?>()
    val selectedMyCard: MutableLiveData<MyCards?> = _selectedMyCard

    fun getMyCards (db: FirebaseFirestore, uid: String?){
        loadMyCards(db, uid ?: "")
        deselectMyCard()
    }
    private fun loadMyCards(db: FirebaseFirestore, uid: String = "") {
        //fetch data from firebase firestore
        db.collection(MyCardsFragment.COLL_MYCARDS)
            .whereEqualTo("clientId", uid).get().addOnFailureListener{
                Log.e("MyCardsViewModel", "Error fetching myCards ${it.message}")
            }.addOnCanceledListener {
                Log.e("MyCardsViewModel", "Cancelled fetching myCards")
            }.addOnSuccessListener {
                val myCardsLoaded = it.toObjects(MyCards::class.java)
                _myCards.value = myCardsLoaded
                Log.d("MyCardsViewModel", "Guest loaded ${myCardsLoaded.size}")
            }
    }
    fun getInviteOfMyCard(){
        //get list of invite from selected my card
        _invite.value = selectedMyCard.value?.invite
    }

    fun selectMyCard(myCards: MyCards) {
        _selectedMyCard.value = myCards
    }
    fun deselectMyCard() {
        _selectedMyCard.value = null
    }
}