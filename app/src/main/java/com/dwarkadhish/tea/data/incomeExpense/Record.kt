package com.dwarkadhish.tea.data.incomeExpense

import com.google.firebase.Timestamp

data class Record(
    val recordId: String = "",
    val income: String = "",
    val expense: String = "",
    val date: String = "",
    val createdAt: Timestamp = Timestamp.now()
)