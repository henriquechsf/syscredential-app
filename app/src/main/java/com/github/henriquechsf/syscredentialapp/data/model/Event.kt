package com.github.henriquechsf.syscredentialapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    var id: String? = null,
    var title: String = "",
    var local: String = "",
    var datetime: String = "",
    var createdAt: String = "",
    var deletedAt: String? = null,
) : Parcelable
