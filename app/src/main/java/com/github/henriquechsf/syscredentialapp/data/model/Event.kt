package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val local: String,
    val datetime: String,
) : Parcelable
