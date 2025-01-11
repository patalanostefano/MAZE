package com.example.maze.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,          // MongoDB ObjectId as String
    val username: String,
    val avatarColor: Int
) : Parcelable