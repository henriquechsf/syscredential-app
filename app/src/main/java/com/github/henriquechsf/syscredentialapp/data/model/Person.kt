package com.github.henriquechsf.syscredentialapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.henriquechsf.syscredentialapp.util.Constants

@Entity(tableName = Constants.PERSON_TABLE_NAME)
data class Person(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val department: String,
    val office: String,
)
