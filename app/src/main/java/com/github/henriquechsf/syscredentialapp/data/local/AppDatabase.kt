package com.github.henriquechsf.syscredentialapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.data.model.EventRegistration
import com.github.henriquechsf.syscredentialapp.data.model.Person

@Database(
    entities = [Event::class, Person::class, EventRegistration::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun personDao(): PersonDao
    abstract fun eventRegistrationDao(): EventRegistrationDao
}