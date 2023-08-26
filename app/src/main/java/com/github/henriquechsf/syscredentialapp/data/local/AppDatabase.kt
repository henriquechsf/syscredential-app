package com.github.henriquechsf.syscredentialapp.data.local

import androidx.room.RoomDatabase

/*
@Database(
    entities = [Event::class, Person::class, Registration::class],
    version = 1,
    exportSchema = false
)
 */
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun personDao(): PersonDao
    abstract fun registrationDao(): RegistrationDao
}