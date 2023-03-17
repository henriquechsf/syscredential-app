package com.github.henriquechsf.syscredentialapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.henriquechsf.syscredentialapp.data.model.Event

@Database(entities = [Event::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}