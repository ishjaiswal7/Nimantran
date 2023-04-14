package com.example.nimantran.models.admin

data class Gift(
    val item: String = "",
    val description: String = "",
    val image: String = "https://wabisabiproject.com/wp-content/uploads/woocommerce-placeholder.png",
    val quantity: Int = 0,
    val price: Double = 0.0,
)
