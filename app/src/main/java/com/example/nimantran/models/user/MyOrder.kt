package com.example.nimantran.models.user

import com.example.nimantran.models.admin.Gift
import com.example.nimantran.models.user.Guest
import com.example.nimantran.models.user.MyCards
import java.sql.Timestamp
import java.util.Date
import java.util.UUID

data class MyOrder(
    val id: String = UUID.randomUUID().toString(),
    val clientID: String = "",
    val orderDate: Date = Timestamp(System.currentTimeMillis()),
    //timestamp
    val orderStatus: String = "",
    //<gift,qty>
    //total amt
    val sentTo: String = "",
    val totalAmount: Double = 0.0,
    val giftQty: String = "",
    val gift: Gift = Gift(),
    val guest: List<Guest> = emptyList(),//= listOf(Guest()),
    val paymentRefId: String = "",
)
