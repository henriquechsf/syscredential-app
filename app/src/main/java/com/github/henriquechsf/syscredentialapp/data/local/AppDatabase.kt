package com.github.henriquechsf.syscredentialapp.data.local

import androidx.room.Database
import com.github.henriquechsf.syscredentialapp.data.model.EventEntity

@Database(entities = [EventEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase {
    abstract fun eventDao(): EventDao
}