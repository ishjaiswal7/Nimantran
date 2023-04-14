package com.example.nimantran.models.user

import java.util.*

data class MyCards(
    val cardID: String = UUID.randomUUID().toString(),
    val cardDate: String = "",
    val eventDate: Date = Date(),
    val cardTitle: String = "",
    val cardImage: String = "https://v4i8g8z4.stackpathcdn.com/wp-content/uploads/2021/06/Naming-Ceremony-Invitation-card-6-375x563.jpg",
)
