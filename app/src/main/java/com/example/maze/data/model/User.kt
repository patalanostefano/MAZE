package com.example.maze.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @DocumentId
    val id: String?,          // Firebase Doc ID
    val username: String,
    val avatarColor: Int
) : Parcelable {
    constructor() :this(null,"",0)
}