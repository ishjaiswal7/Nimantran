package com.example.nimantran.models.user

data class Invite(
    val guestId: String,
    val guestName: String,
    val phone: String,
    val response: String = "Not Responded",
)
