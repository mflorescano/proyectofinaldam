package com.mario.citas.ui.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mario.citas.ui.models.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class UserDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
}