package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Person(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    @ColumnInfo(name = "registration_code")
    val registrationCode: Long,
    val info1: String? = "",
    val info2: String? = "",
) : Parcelable
