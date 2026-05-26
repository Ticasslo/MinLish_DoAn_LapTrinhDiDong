package com.example.englishapp.core.data.mapper

import com.example.englishapp.core.data.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toDomain(): User {
    return User(
        userId = this.uid,
        name = this.displayName ?: "",
        email = this.email ?: "",
        avatar = this.photoUrl?.toString()
    )
}
