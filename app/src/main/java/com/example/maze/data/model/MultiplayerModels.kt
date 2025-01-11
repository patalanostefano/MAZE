package com.example.maze.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class GameInvite(
    val fromUser: User,
    val toUser: User,
    val gameId: String = UUID.randomUUID().toString()
) : Parcelable