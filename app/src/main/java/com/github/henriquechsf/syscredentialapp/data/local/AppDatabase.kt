package com.github.henriquechsf.syscredentialapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.data.model.Person
import com.github.henriquechsf.syscredentialapp.data.model.Registration

@Database(
    entities = [Event::class, Person::class, Registration::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun personDao(): PersonDao
    abstract fun registrationDao(): RegistrationDao
}