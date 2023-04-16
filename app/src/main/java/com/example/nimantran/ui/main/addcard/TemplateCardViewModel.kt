package com.example.nimantran.ui.main.addcard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.user.MyCards
import com.example.nimantran.models.user.Template
import com.example.nimantran.ui.main.mycards.MyCardsFragment
import com.google.firebase.firestore.FirebaseFirestore

class TemplateCardViewModel : ViewModel() {

    private val _mycards = MutableLiveData<List<MyCards>>()
    val mycards: LiveData<List<MyCards>> = _mycards

    private val _templates = MutableLiveData<List<Template>>()
    val templates: MutableLiveData<List<Template>>
        get() = _templates

    private val _selectedTemplate = MutableLiveData<Template>()
    val selectedTemplate: MutableLiveData<Template>
        get() = _selectedTemplate

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

    fun getMyCards(db: FirebaseFirestore){
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

}