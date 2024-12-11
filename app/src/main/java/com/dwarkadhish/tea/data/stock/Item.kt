package com.dwarkadhish.tea.data.stock

data class Item(
    val id: String = "",
    val categoryName: String = "",
    val stockQty: String = "",
    val stockPrice: String = "",
    val entryDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
