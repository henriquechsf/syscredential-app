package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.henriquechsf.syscredentialapp.util.Constants
import kotlinx.parcelize.Parcelize

@Entity(tableName = Constants.EVENT_REGISTRATIONS_TABLE_NAME)
data class EventRegistration(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "event_id")
    val eventId: Int,

    @ColumnInfo(name = "person_id")
    val personId: Long,

    @ColumnInfo(name = "created_at")
    val createdAt: String,
)
