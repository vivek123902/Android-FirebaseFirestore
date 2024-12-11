package com.dwarkadhish.tea.data.teaManagement
import com.google.firebase.Timestamp

data class TeaCount(
    val date: Timestamp? = null,  // Firestore Timestamp for the date
    val teaCount: Int? = null      // Tea count for that date
)

