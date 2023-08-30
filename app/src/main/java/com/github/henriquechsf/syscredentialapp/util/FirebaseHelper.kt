package com.github.henriquechsf.syscredentialapp.util

import com.github.henriquechsf.syscredentialapp.R
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

// TODO: Mover implementacao para classe repository
class FirebaseHelper {

    companion object {
        fun isAuthenticated(): Boolean = FirebaseAuth.getInstance().currentUser != null

        fun getUserId() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        fun getAuth() = FirebaseAuth.getInstance()

        fun getUUID() = UUID.randomUUID().toString()

        // TODO: Adicionar demais mapeamentos de erro Firebase
        fun validError(error: String): Int {
            return when {
                error.contains("There is no user record corresponding to this identifier.") -> {
                    R.string.account_not_registered_register_fragment
                }
                error.contains("The email address is badly formatted.") -> {
                    R.string.invalid_email_or_password_register_fragment
                }
                error.contains("The password is invalid or the user does not have a password.") -> {
                    R.string.invalid_email_or_password_register_fragment
                }
                else -> R.string.common_error_message_generic
            }
        }
    }
}