package com.github.henriquechsf.syscredentialapp.data.model

data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var image: String = "",
    var password: String = ""
)