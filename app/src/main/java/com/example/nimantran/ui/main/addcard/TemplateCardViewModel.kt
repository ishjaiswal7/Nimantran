package com.example.nimantran.ui.main.addcard

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.user.Guest
import com.example.nimantran.models.user.Invite
import com.example.nimantran.models.user.MyCards
import com.example.nimantran.models.user.Template
import com.example.nimantran.ui.main.mycards.MyCardsFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class TemplateCardViewModel : ViewModel() {

    private val _mycards = MutableLiveData<List<MyCards>>()
    val mycards: LiveData<List<MyCards>> = _mycards

    private val _templates = MutableLiveData<List<Template>>()
    val templates: MutableLiveData<List<Template>>
        get() = _templates

    private val _selectedTemplate = MutableLiveData<Template>()
    val selectedTemplate: MutableLiveData<Template>
        get() = _selectedTemplate

    private val _invitationCard = MutableLiveData<Uri>()
    val invitationCard: MutableLiveData<Uri>
        get() = _invitationCard

    private val _cardUrl = MutableLiveData<String>()
    val cardUrl: MutableLiveData<String>
        get() = _cardUrl

    private val _isSaved = MutableLiveData<Boolean>(false)
    val isSaved: MutableLiveData<Boolean>
        get() = _isSaved

    private val _inviteList = MutableLiveData<List<Invite>>()
    val inviteList: MutableLiveData<List<Invite>>
        get() = _inviteList


    fun selectTemplate(template: Template) {
        _selectedTemplate.value = template
    }

    fun getTemplates(db: FirebaseFirestore) {
        db.collection("templates").get().addOnSuccessListener { result ->
            val templatesLoaded = result.toObjects(Template::class.java)
            _templates.value = templatesLoaded
            Log.d("TemplateCardViewModel", "Templates loaded ${templatesLoaded.size}")
        }.addOnFailureListener { exception ->
            Log.e("TemplateCardViewModel", "Error fetching templates ${exception.message}")
        }
    }

    fun getMyCards(db: FirebaseFirestore) {
        loadMyCards(db)
    }

    private fun loadMyCards(db: FirebaseFirestore) {
        // fetch data from firebase firestore
        db.collection(MyCardsFragment.COLL_MYCARDS).get().addOnFailureListener {
            Log.e("TemplateCardViewmodel", "Error fetching mycards ${it.message}")
        }.addOnCanceledListener {
            Log.e("TemplateCardViewmodel", "Cancelled fetching mycards")
        }.addOnSuccessListener {
            val mycardsLoaded = it.toObjects(MyCards::class.java)
            _mycards.value = mycardsLoaded
            Log.d("TemplateCardViewmodel", "MyCards loaded ${mycardsLoaded.size}")
        }

    }

    fun sendInvitation(
        context: Context,
        storage: FirebaseStorage,
        db: FirebaseFirestore,
        uid: String,
        rsvp: String,
        phone: String,
        date: String,
        title: String,
        message: String,
        guestList: List<Guest>,
        invitationCardUri: String
    ) {

        // upload invitation card to firebase storage
        val storageRef = storage.reference
        val invitationCardRef = storageRef.child("invitation_cards/$uid")
        invitationCardRef.putFile(Uri.parse(invitationCardUri)).addOnSuccessListener {
            Log.d("TemplateCardViewmodel", "Invitation card uploaded")
            // get download url
            invitationCardRef.downloadUrl.addOnSuccessListener { uri ->
                Log.d("TemplateCardViewmodel", "Invitation card download url $uri")
                _cardUrl.value = uri.toString()
                // save invitation card to firebase firestore
                _inviteList.value = guestList.map {
                    Invite(
                        guestId = it.id,
                        guestName = it.name,
                        phone = it.phone,
                        response = "Not Responded",
                    )
                }
                val mycards = MyCards(
                    clientId = uid,
                    cardTitle = title,
                    cardMessage = message,
                    rsvp = rsvp,
                    phone = phone,
                    invite = _inviteList.value!!,
                    eventDate = date,
                    cardImage = uri.toString()
                )
                db.collection(COLL_MYCARDS).document(uid).set(mycards).addOnSuccessListener {
                    Log.d("TemplateCardViewmodel", "Invitation saved to firestore   ")

                }.addOnFailureListener {
                    Log.e("TemplateCardViewmodel", "Error sending invitation ${it.message}")
                }
            }.addOnFailureListener {
                Log.e(
                    "TemplateCardViewmodel",
                    "Error getting invitation card download url ${it.message}"
                )
            }
        }.addOnFailureListener {
            Log.e("TemplateCardViewmodel", "Error uploading invitation card ${it.message}")
        }
    }

    fun setInvitationCard(uri: Uri) {
        _invitationCard.value = uri
    }

    companion object {
        const val COLL_MYCARDS = "mycards"
    }
}