package com.example.primecare.Room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["firebaseId"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firebaseId: String,
    val firstName: String,
    val lastName: String,
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val genre: String = "",
    val age: Int = 0
)