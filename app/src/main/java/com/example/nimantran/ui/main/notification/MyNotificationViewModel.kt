package com.example.nimantran.ui.main.notification
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nimantran.models.admin.Notification
import com.google.firebase.firestore.FirebaseFirestore

class MyNotificationViewModel : ViewModel() {
    private val _myNotifications = MutableLiveData<List<Notification>>()
    val myNotifications: MutableLiveData<List<Notification>> = _myNotifications

    private val _selectedMyNotification = MutableLiveData<Notification?>()
    val selectedMyNotification: MutableLiveData<Notification?> = _selectedMyNotification

    private val _isLoading = MutableLiveData(false)
    val isLoading: MutableLiveData<Boolean> = _isLoading

    fun getMyNotifications(db: FirebaseFirestore) {
        loadMyNotifications(db)
        deselectMyNotification()
    }

    private fun loadMyNotifications(db: FirebaseFirestore) {
        // fetch data from firebase firestore
        db.collection(MyNotificationFragment.COLL_MY_NOTIFICATIONS).get().addOnFailureListener {
            Log.e("MyNotificationViewModel", "Error fetching notifications ${it.message}")
        }.addOnCanceledListener {
            Log.e("MyNotificationViewModel", "Cancelled fetching notifications")
        }.addOnSuccessListener {
            val notificationsLoaded = it.toObjects(Notification::class.java)
            _myNotifications.value = notificationsLoaded
            Log.d("MyNotificationViewModel", "Notification loaded ${notificationsLoaded.size}")
        }
    }

    fun selectMyNotification(notification: Notification) {
        _selectedMyNotification.value = notification
    }

    fun deselectMyNotification() {
        _selectedMyNotification.value = null
    }

    fun deleteMyNotification(db: FirebaseFirestore, notification: Notification) {
        db.collection(MyNotificationFragment.COLL_MY_NOTIFICATIONS).document(notification.id)
            .delete().addOnFailureListener {
                Log.e("MyNotificationViewModel", "Error deleting notification ${it.message}")
            }.addOnCanceledListener {
                Log.e("MyNotificationViewModel", "Cancelled deleting notification")
            }.addOnSuccessListener {
                Log.d("MyNotificationViewModel", "Notification deleted")
            }
    }
}
