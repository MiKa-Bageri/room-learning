package com.md.room_learning
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    val firstName:String,
    val lastName:String,
    val phone:String,
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
)
