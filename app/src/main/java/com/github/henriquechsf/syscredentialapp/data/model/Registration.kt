package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import androidx.room.PrimaryKey
import com.github.henriquechsf.syscredentialapp.util.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = Constants.REGISTRATIONS_TABLE_NAME, foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["event_id"],
        ),
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["person_id"],
        )
    ]
)
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
