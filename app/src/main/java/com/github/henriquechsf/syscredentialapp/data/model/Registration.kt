package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Registration(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "event_id")
    val eventId: Int,

    @ColumnInfo(name = "person_id")
    val personId: Long,
) : Parcelable
