package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Registration(
    val id: Int = 0,
    val createdAt: String,
    val eventId: Int,
    val personId: Long,
) : Parcelable
