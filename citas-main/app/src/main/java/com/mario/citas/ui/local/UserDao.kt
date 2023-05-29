package com.mario.citas.ui.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mario.citas.ui.models.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity")
    fun getAll(): List<UserEntity>

    @Insert
    fun insert(vararg users: UserEntity)

    @Delete
    fun delete(user: UserEntity)
}