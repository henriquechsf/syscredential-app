package com.github.henriquechsf.syscredentialapp.data.repository.profile

import android.net.Uri
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.util.FirebaseHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class ProfileRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) : ProfileRepository {

    private val profileDatabaseRef = database.reference
        .child("profile")

    private val profileStorageRef = storage.reference
        .child("images")
        .child("profiles")
        .child("${FirebaseHelper.getUserId()}.jpeg")

    override suspend fun getProfile(userId: String): User {
        return suspendCoroutine { continuation ->
            profileDatabaseRef
                .child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            continuation.resumeWith(Result.success(it))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWith(Result.failure(error.toException()))
                    }

                })
        }
    }


    override suspend fun saveProfile(user: User) {
        return suspendCoroutine { continuation ->
            profileDatabaseRef
                .child(user.id)
                .setValue(user)
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

    override suspend fun getProfileList(): List<User> {
        return suspendCoroutine { continuation ->
            profileDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userList = mutableListOf<User>()

                    snapshot.children.forEach { dataSnapshot ->
                        val user = dataSnapshot.getValue(User::class.java)
                        user?.let { userList.add(it) }
                    }

                    continuation.resumeWith(
                        Result.success(
                            userList.apply {
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

    override suspend fun saveImageProfile(image: String): String {
        return suspendCoroutine { continuation ->
            val uploadTask = profileStorageRef.putFile(Uri.parse(image))
            uploadTask.addOnSuccessListener {
                profileStorageRef.downloadUrl.addOnCompleteListener { task ->
                    continuation.resumeWith(Result.success(task.result.toString()))
                }
            }.addOnFailureListener {
                continuation.resumeWith(Result.failure(it))
            }
        }
    }
}