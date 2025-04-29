package com.example.primecare.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    // Query to get the first user in the database
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getFirstUser(): User?
}
