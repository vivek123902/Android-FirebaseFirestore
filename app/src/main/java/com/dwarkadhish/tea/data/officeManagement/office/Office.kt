package com.dwarkadhish.tea.data.officeManagement.office

data class Office(
    val officeId: String = "",
    val officeName: String = "",
    val floorId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
