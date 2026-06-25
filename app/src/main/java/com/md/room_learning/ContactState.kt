package com.md.room_learning

data class ContactState(
    val contacts: List<Contact> = emptyList(),
    val sortType: SortType = SortType.FIRST_NAME,
    val isDialogVisible: Boolean = false,
    val firstName: String="",
    val lastName: String="",
    val phoneNumber: String=""
)
