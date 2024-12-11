package com.dwarkadhish.tea.data.officeManagement.floor

import com.google.firebase.Timestamp

data class Floor(
    val floorId: String = "",
    val floorName: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
