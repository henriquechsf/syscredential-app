package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Person(
    val id: Long = 0L,
    val name: String,
    val registrationCode: Long,
    val info1: String? = "",
    val info2: String? = "",
) : Parcelable
