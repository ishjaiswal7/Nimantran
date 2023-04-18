package com.example.nimantran.ui.authentication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.user.MyCards
import com.google.firebase.firestore.FirebaseFirestore

class GuestResponseViewModel: ViewModel() {
    private val _card = MutableLiveData<String>()
    val card: MutableLiveData<String> = _card

    var verifiedGuest: Boolean = false
    var inviteCardImage: String = ""
    var inviteCardTitle: String = ""
    var inviteEventDate: String = ""
    var inviteMessage: String = ""
    var inviteRSVP: String = ""
    var invitePhone: String = ""

    var inviteGuestResponse: String = ""

    //list of all myCards
    var myCardsList = mutableListOf<MyCards>()
    private fun loadMyCards(db: FirebaseFirestore) {
        // fetch myCards from firebase firestore
        db.collection("mycards")
            .get().addOnFailureListener {
                Log.e("GuestResponseViewModel", "Error fetching myCards ${it.message}")
            }.addOnCanceledListener {
                Log.e("GuestResponseViewModel", "Cancelled fetching myCards")
            }.addOnSuccessListener {
                val myCardsLoaded = it.toObjects(MyCards::class.java)
                _card.value = myCardsLoaded.toString()
                Log.d("GuestResponseViewModel", "myCards loaded ${myCardsLoaded.size}")
                myCardsList = myCardsLoaded.toMutableList()
            }
    }
    fun searchCardGuest(db: FirebaseFirestore, cardId: String, guestPhone: String){
        loadMyCards(db)
        for (card in myCardsList){
            if(card.cardID == cardId){
                for (invite in card.invite){
                    if(invite.phone == guestPhone){
                        verifiedGuest = true
                        inviteCardImage = card.cardImage
                        inviteCardTitle = card.cardTitle
                        inviteEventDate = card.eventDate
                        inviteMessage = card.cardMessage
                        inviteRSVP = card.rsvp
                        invitePhone = invite.phone
                        break
                    }
                }
            }
        }
    }

    fun resetCardDetails() {
        verifiedGuest = false
        inviteCardImage = ""
        inviteCardTitle = ""
        inviteEventDate = ""
        inviteMessage = ""
        inviteRSVP = ""
        invitePhone = ""
    }
}