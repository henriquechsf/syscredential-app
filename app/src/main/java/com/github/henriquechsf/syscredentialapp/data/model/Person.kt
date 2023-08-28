package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.henriquechsf.syscredentialapp.util.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = Constants.PERSON_TABLE_NAME)
data class Person(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    @ColumnInfo(name = "registration_code")
    val registrationCode: Long,
    val info1: String? = "",
    val info2: String? = "",
) : Parcelable
