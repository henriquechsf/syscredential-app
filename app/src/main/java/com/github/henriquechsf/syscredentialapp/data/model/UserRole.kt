package com.github.henriquechsf.syscredentialapp.data.model

import androidx.annotation.StringRes
import com.github.henriquechsf.syscredentialapp.R

enum class UserRole(@StringRes val translation: Int) {
    ADMIN(R.string.user_role_admin_user_list_fragment),
    PARTICIPANT(R.string.user_role_participant_user_list_fragment)
}