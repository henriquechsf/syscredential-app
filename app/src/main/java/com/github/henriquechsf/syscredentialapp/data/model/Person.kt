package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
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
    val email: String? = "",
    val cellphone: String? = "",
) : Parcelable
