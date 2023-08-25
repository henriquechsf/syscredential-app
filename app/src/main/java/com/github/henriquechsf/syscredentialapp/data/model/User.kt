package com.github.henriquechsf.syscredentialapp.data.model

import com.google.firebase.database.Exclude

data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var image: String = "",
    @get:Exclude
    var password: String = ""
)