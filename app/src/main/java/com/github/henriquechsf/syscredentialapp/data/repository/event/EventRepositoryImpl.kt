package com.github.henriquechsf.syscredentialapp.data.repository.event

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

    override suspend fun saveEvent(event: Event) {
        return suspendCoroutine { continuation ->
            eventDatabaseRef
                .child(event.id!!)
                .setValue(event)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resumeWith(Result.success(Unit))
                    } else {
                        task.exception?.let {
                            continuation.resumeWith(Result.failure(it))
                        }
                    }
                }
        }
    }

    override suspend fun removeEvent(event: Event) {
        return suspendCoroutine { continuation ->
            eventDatabaseRef
                .child(event.id!!)
                .removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resumeWith(Result.success(Unit))
                    } else {
                        task.exception?.let {
                            continuation.resumeWith(Result.failure(it))
                        }
                    }
                }
        }
    }

    // TODO: update to real time changes
    override suspend fun getEventList(): List<Event> {
        return suspendCoroutine { continuation ->
            eventDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val eventList = mutableListOf<Event>()

                    snapshot.children.forEach { dataSnapshot ->
                        val event = dataSnapshot.getValue(Event::class.java)
                        event?.let { eventList.add(it) }
                    }

                    continuation.resumeWith(
                        Result.success(
                            eventList.apply {
                                removeAll { it.deletedAt.isNotEmpty() }
                            }
                        )
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWith(Result.failure(error.toException()))
                }
            })
        }
    }
}