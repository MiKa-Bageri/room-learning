@file:OptIn(ExperimentalCoroutinesApi::class)

package com.md.room_learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactViewModel(
    private val dao: ContactDao
): ViewModel() {
    private val _state = MutableStateFlow(ContactState())
    private val _sortType = MutableStateFlow(SortType.FIRST_NAME)
    private val _contacts = _sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.FIRST_NAME -> dao.getContactsOrderByFirstName()
                SortType.LAST_NAME -> dao.getContactsOrderByLastName()
                SortType.PHONE_NUMBER -> dao.getContactsOrderByPhoneNumber()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val state = combine(_state, _sortType, _contacts) { state, sortType, contacts ->
        state.copy(
            contacts = contacts,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())
    fun onEvent(event: ContactEvent){
        when(event){
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.delete(event.contact)
                }
            }
            ContactEvent.HideDialog -> {
                _state.update {
                    it.copy(isDialogVisible = false)
                }
            }
            ContactEvent.SaveContact -> TODO()
            is ContactEvent.SetFirstName -> {
                _state.update {it.copy(
                    firstName = event.firstName
                )}
            }
            is ContactEvent.SetLastName -> {
                _state.update {it.copy(
                    lastName = event.lastName
                )}
            }
            is ContactEvent.SetPhoneNumber -> {
                _state.update {it.copy(
                    phoneNumber = event.phoneNumber
                )}
            }
            ContactEvent.ShowDialog -> {
                _state.update {
                    it.copy(isDialogVisible = true)
                }
            }
            is ContactEvent.SortContacts -> {
                _sortType.value = event.sortType
            }
            is ContactEvent.SaveContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber

                if(firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()) {
                    return
                }

                val contact = Contact(
                    firstName = firstName,
                    lastName = lastName,
                    phone = phoneNumber
                )
                viewModelScope.launch {
                    dao.insert(contact)
                }
                _state.update { it.copy(
                    isDialogVisible = false,
                    firstName = "",
                    lastName = "",
                    phoneNumber = ""
                ) }
            }
        }
    }
}