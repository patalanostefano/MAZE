// ui/screens/gameplay/components/PlayerBall.kt
package com.example.maze.ui.screens.gameplay.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.maze.data.model.Position

@Composable
fun PlayerBall(
    position: Position,
    color: Color = Color.Blue,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawCircle(
            color = color,
            radius = 15.dp.toPx(),
            center = Offset(position.x, position.y)
        )
    }
}
