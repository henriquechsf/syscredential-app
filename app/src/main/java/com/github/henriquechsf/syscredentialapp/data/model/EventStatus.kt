package com.github.henriquechsf.syscredentialapp.data.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.github.henriquechsf.syscredentialapp.R

enum class EventStatus(@StringRes val translation: Int, @ColorRes val color: Int) {
    TODAY(R.string.today, R.color.red),
    SCHEDULED(R.string.scheduled, R.color.primary_variant),
    FINISHED(R.string.finished, R.color.teal_200)
}