package com.github.henriquechsf.syscredentialapp.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formatDateString(date: String, withTime: Boolean = true): String {
    var pattern = "dd/MM/yyyy"
    if (withTime) pattern = "$pattern HH:mm"
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val dateTime = LocalDateTime.parse(date)
    return dateTime.format(formatter)
}

fun formatTime(date: String): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val dateTime = LocalDateTime.parse(date)
    return dateTime.format(formatter)
}