package com.example.nimantran.models.user

import java.util.*

data class Guest(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
//    val email: String = "",
    val id: String = UUID.randomUUID().toString(),
    val clientId: String = ""
)
