package com.example.nimantran.models.user

import java.util.*

data class MyCards(
    val clientId: String = "",
    val cardTitle: String = "",
    val eventDate: String = "",
    val cardMessage: String = "",
    val rsvp: String = "",
    val phone: String = "",
    val invite: List<Invite> = emptyList(),
    val cardID: String = UUID.randomUUID().toString(),
    val cardImage: String = "https://v4i8g8z4.stackpathcdn.com/wp-content/uploads/2021/06/Naming-Ceremony-Invitation-card-6-375x563.jpg",
    val cardDate: String = Calendar.getInstance().time.toString(),
)
