package com.github.henriquechsf.syscredentialapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.henriquechsf.syscredentialapp.util.Constants

@Entity(tableName = Constants.EVENTS_TABLE_NAME)
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val description: String,
    val local: String,
    val datetime: Long
)
