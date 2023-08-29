package com.github.henriquechsf.syscredentialapp.data.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.github.henriquechsf.syscredentialapp.R

enum class UserStatus(@StringRes val translation: Int, @ColorRes val color: Int) {
    ACTIVE(R.string.user_status_active_user_list_fragment, R.color.green_300),
    INACTIVE(R.string.user_status_inactive_user_list_fragment, R.color.dark),
    BLOCKED(R.string.user_status_blocked_user_list_fragment, R.color.red_500)
}