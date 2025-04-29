package com.example.primecare.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val weight: Double,
    val height: Double,
    val genre: String,
    val age: Int
)
