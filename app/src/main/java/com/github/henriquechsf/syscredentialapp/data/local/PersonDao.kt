package com.github.henriquechsf.syscredentialapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.henriquechsf.syscredentialapp.data.model.Person
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(event: Person): Long

    @Query("SELECT * FROM persons WHERE name LIKE :query || '%'")
    fun getAll(query: String? = ""): Flow<List<Person>>

    @Query("SELECT * from persons where id = :personId")
    suspend fun getById(personId: Long): Person?

    @Query("SELECT * from persons where registration_code = :registrationCode")
    suspend fun getByRegistrationCode(registrationCode: Long): Person?

    @Delete
    suspend fun delete(event: Person)
}