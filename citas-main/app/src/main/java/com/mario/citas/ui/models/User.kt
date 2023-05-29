package com.mario.citas.ui.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val userID: String? = null,
    val email: String,
    val name: String,
    val contact: String
) : Parcelable

fun User.toEntity() = UserEntity(email = email, name = name, contact = contact)