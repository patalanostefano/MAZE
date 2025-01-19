package com.example.maze.data.model

import android.net.Uri
import androidx.compose.ui.graphics.Color

data class Avatar(
    val color: Color = Color.Transparent,
    val imageUri: Uri? = null
)
