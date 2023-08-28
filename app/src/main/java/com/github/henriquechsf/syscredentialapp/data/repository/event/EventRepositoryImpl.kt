package com.github.henriquechsf.syscredentialapp.data.repository.event

import android.util.Log
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class EventRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : EventRepository {

    private val eventDatabaseRef = database.reference
        .child("event")

    override suspend fun getAll(query: String?): List<Event> {
        return suspendCoroutine { continuation ->
            eventDatabaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val eventEntityList = mutableListOf<Event>()

                    snapshot.children.forEach { dataSnapshot ->
                        val eventEntity = dataSnapshot.getValue(Event::class.java)
                        eventEntity?.let { eventEntityList.add(it) }
                    }
                    Log.i("INFOTEST", "onDataChange: $eventEntityList")

                    continuation.resumeWith(Result.success(eventEntityList))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWith(Result.failure(error.toException()))
                }
            })
        }
    }

    override suspend fun getById(id: String): Event? {
        TODO("Not yet implemented")
    }

    override suspend fun insert(eventEntity: Event) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(eventEntity: Event) {
        TODO("Not yet implemented")
    }
}