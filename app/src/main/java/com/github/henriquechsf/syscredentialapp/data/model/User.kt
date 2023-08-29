package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize
import java.util.Date

// TODO: refactor to val attributes
@Parcelize
data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    // TODO: implement phone user data with input mask
    var phone: String = "",
    var image: String = "",
    @get:Exclude
    var password: String = "",
    var department: String = "",
    var status: UserStatus = UserStatus.BLOCKED,
    val createdAt: String = Date().toString(),
    var role: UserRole = UserRole.PARTICIPANT
) : Parcelable