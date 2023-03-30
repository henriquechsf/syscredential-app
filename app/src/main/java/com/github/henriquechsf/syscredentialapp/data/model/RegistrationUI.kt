package com.github.henriquechsf.syscredentialapp.data.model

data class RegistrationUI(
    val id: Int = 0,
    val createdAt: String,
    val eventId: Int,
    val personId: Long,
    val personName: String,
    val personInfo1: String? = "",
)
