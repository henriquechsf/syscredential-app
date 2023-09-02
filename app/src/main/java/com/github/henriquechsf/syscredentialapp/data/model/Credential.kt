package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Credential(
    var id: String = "",
    var createdAt: String = "",
    var eventId: String = "",
    var userId: String = "",
    var userName: String = "",
    var userDepartment: String = "",
    var userImage: String = "",
    var isRegistered: Boolean = false,
    var registeredAt: String = "",
) : Parcelable
