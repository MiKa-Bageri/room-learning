package com.md.room_learning

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Upsert
    suspend fun insert(contact: Contact)
    @Query("SELECT * FROM contact")
    fun getAll(): List<Contact>
    @Query("SELECT * FROM contact WHERE id = :id")
    fun getContact(id: Int): Contact
    @Query("SELECT * FROM contact ORDER BY firstName ASC")
    fun getContactsOrderByFirstName(): Flow<List<Contact>>
    @Query("SELECT * FROM contact ORDER BY lastName ASC")
    fun getContactsOrderByLastName(): Flow<List<Contact>>
    @Query("SELECT * FROM contact ORDER BY phone ASC")
    fun getContactsOrderByPhoneNumber(): Flow<List<Contact>>
    @Delete
    suspend fun delete(contact: Contact)
}