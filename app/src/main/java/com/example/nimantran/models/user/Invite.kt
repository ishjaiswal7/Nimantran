package com.example.nimantran.models.user

import java.util.*

data class Invite(
    val clientId: String,
    val guestname: String,
    val phone: String,
    val address: String,
    val gid: String,
    val message: String = "",
    val date: String = Calendar.getInstance().time.toString(),
)
