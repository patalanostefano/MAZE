// ui/screens/gameplay/components/Labyrinth2DRenderer.kt
package com.example.maze.ui.screens.gameplay.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.Bitmap
import com.example.maze.data.model.Position
import com.example.maze.utils.ImageUtils

@Composable
fun Labyrinth2DRenderer(
    fullImageUrl: String,
    playerPosition: Position,
    modifier: Modifier = Modifier
) {
    var visibleQuadrant by remember { mutableIntStateOf(0) }
    var croppedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Calculate which quadrant the player is in and update the visible portion
    LaunchedEffect(playerPosition) {
        val fullBitmap = ImageUtils.decodeBase64ToBitmap(fullImageUrl)
        fullBitmap?.let {
            val width = it.width
            val height = it.height

            // Determine quadrant (0: top-left, 1: top-right, 2: bottom-left, 3: bottom-right)
            val quadrantX = if (playerPosition.x < width / 2) 0 else 1
            val quadrantY = if (playerPosition.y < height / 2) 0 else 1
            val newQuadrant = quadrantY * 2 + quadrantX

            if (newQuadrant != visibleQuadrant) {
                visibleQuadrant = newQuadrant

                // Crop the bitmap to show only the current quadrant
                val startX = (quadrantX * width / 2)
                val startY = (quadrantY * height / 2)
                croppedBitmap = Bitmap.createBitmap(
                    fullBitmap,
                    startX,
                    startY,
                    width / 2,
                    height / 2
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        croppedBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Maze",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}
