package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.henriquechsf.syscredentialapp.util.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = Constants.EVENTS_TABLE_NAME)
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val local: String,
    val datetime: String,
) : Parcelable
