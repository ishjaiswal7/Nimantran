package com.example.nimantran.ui.authentication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.user.Invite
import com.example.nimantran.models.user.MyCards
import com.google.firebase.firestore.FirebaseFirestore

class GuestResponseViewModel: ViewModel() {
    private val _cards = MutableLiveData<List<MyCards>>()
    val cards: MutableLiveData<List<MyCards>> = _cards

    private val _selectedCard = MutableLiveData<MyCards>()
    val selectedCard: MutableLiveData<MyCards> = _selectedCard

    private val _selectedInvite = MutableLiveData<Invite>()
    val selectedInvite: MutableLiveData<Invite> = _selectedInvite
    fun loadCards(db: FirebaseFirestore){
        loadMyCards(db)
    }
    private fun loadMyCards(db: FirebaseFirestore) {
        // fetch myCards from firebase firestore
        db.collection("mycards")
            .get().addOnFailureListener {
                Log.e("GuestResponseViewModel", "Error fetching myCards ${it.message}")
            }.addOnCanceledListener {
                Log.e("GuestResponseViewModel", "Cancelled fetching myCards")
            }.addOnSuccessListener {
                val myCardsLoaded = it.toObjects(MyCards::class.java)
                _cards.value = myCardsLoaded
                Log.d("GuestResponseViewModel", "myCards loaded ${myCardsLoaded.size}")
              //  myCardsList = myCardsLoaded.toMutableList()
            }
    }
    fun selectCard(cardId: String){
        if(cards.value != null){
            for (card in cards.value!!){
                if(card.cardID == cardId){
                    _selectedCard.value = card
                    Log.d("GuestResponseViewModel", "Card Selected ${card.cardID}")
                    break
                }
            }
        }
        else{
            Log.d("GuestResponseViewModel", "No Cards Loaded")
        }
    }
    fun searchCardGuest(guestPhone: String){
                //for each invite in card
        if(guestPhone != null){
            for (invite in selectedCard.value!!.invite){
                //if phone number matches
                if(invite.phone == guestPhone){
                    //set verifiedGuest to true
                    _selectedInvite.value = invite
                    Log.d("GuestResponseViewModel", "Invite Selected ${invite.guestName}")
                    break
                }
            }
        }
    }
    fun updateMyCardInviteResponse(db: FirebaseFirestore, response: String){
        //firebase query to update response
        db.collection("mycards").document(selectedCard.value!!.cardID).collection("invite").document(selectedInvite.value!!.phone).update("response", response)
            .addOnSuccessListener {
                Log.d("GuestResponseViewModel", "Response Updated")
            }.addOnFailureListener {
                Log.e("GuestResponseViewModel", "Error updating response ${it.message}")
            }
    }
}