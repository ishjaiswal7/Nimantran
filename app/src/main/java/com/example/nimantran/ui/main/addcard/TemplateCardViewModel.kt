package com.example.nimantran.ui.main.addcard

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.user.Template
import com.google.firebase.firestore.FirebaseFirestore

class TemplateCardViewModel : ViewModel() {
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


}