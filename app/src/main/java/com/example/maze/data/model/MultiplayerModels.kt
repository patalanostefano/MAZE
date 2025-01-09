package com.example.maze.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(
    val id: String,
    val username: String,
    val avatarColor: Int
) : Parcelable

@Parcelize
data class GameInvite(
    val fromPlayer: Player,
    val toPlayer: Player,
    val gameId: String = java.util.UUID.randomUUID().toString()
) : Parcelable {
    // Add required Parcelable implementation
    override fun describeContents(): Int = 0
}
