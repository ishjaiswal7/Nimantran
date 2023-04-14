package com.example.nimantran.models.admin

import java.util.*

data class Client(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val phone: String? = "",
    val gender: String= "",
    val profileImg: String= "https://firebasestorage.googleapis.com/v0/b/nimantran-8260d.appspot.com/o/clients%2Fprofile-user.png?alt=media&token=ed599da2-984a-4b1e-bf68-a08d0f4852c6",
)
