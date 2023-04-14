package com.example.nimantran.models.admin

import com.example.nimantran.models.user.MyCards
import com.example.nimantran.models.user.MyOrder
import java.util.Date
import java.util.UUID

data class Order(
    val id: String = "",
    val client: Client = Client(),
    val myOrder: MyOrder = MyOrder(),
)
