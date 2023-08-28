package com.github.henriquechsf.syscredentialapp.data.repository.auth

import com.github.henriquechsf.syscredentialapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class AuthFirebaseRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthFirebaseRepository {
    override suspend fun login(email: String, password: String) {
        return suspendCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
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

    override suspend fun register(user: User): User {
        return suspendCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resumeWith(Result.success(user))
                    } else {
                        task.exception?.let {
                            continuation.resumeWith(Result.failure(it))
                        }
                    }
                }
        }
    }

    // TODO: format template email of Firebase
    override suspend fun forgot(email: String) {
        return suspendCoroutine { continuation ->
            firebaseAuth.sendPasswordResetEmail(email)
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
}