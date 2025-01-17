package com.example.maze.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.example.maze.data.model.Position

// utils/WallDetectionUtil.kt
object WallDetectionUtil {
    fun isWall(bitmap: Bitmap, x: Int, y: Int): Boolean {
        val pixel = bitmap.getPixel(x, y)
        val brightness = Color(pixel).luminance()
        return brightness < 0.5f  // Consider dark pixels as walls
    }

    fun canMove(bitmap: Bitmap, newPosition: Position): Boolean {
        // Check if the new position would collide with a wall
        val radius = 20 // Ball radius in pixels

        // Check points around the ball's circumference
        for (angle in 0 until 360 step 45) {
            val checkX = (newPosition.x + radius * kotlin.math.cos(Math.toRadians(angle.toDouble()))).toInt()
            val checkY = (newPosition.y + radius * kotlin.math.sin(Math.toRadians(angle.toDouble()))).toInt()

            if (isWall(bitmap, checkX, checkY)) {
                return false
            }
        }
        return true
    }
}
