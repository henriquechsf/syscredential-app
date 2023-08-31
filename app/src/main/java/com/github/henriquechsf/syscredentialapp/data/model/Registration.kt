package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Registration(
    val id: String,
    val eventId: String,
    val userId: String,
    val createdAt: String,
) : Parcelable
