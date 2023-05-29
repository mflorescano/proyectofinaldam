package com.mario.citas.ui.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    val email: String? = null,
    val name: String? = null,
    val contact: String? = null
)

fun UserEntity.toView(userID: String) =
    User(
        userID = userID,
        email = email.orEmpty(),
        name = name.orEmpty(),
        contact = contact.orEmpty()
    )