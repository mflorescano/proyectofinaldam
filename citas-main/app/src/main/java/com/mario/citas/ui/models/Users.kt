package com.mario.citas.ui.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    val userList: List<User>
) : Parcelable
