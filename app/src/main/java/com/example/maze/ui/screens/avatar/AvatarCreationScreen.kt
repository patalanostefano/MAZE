package com.example.maze.ui.screens.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.maze.data.model.UserContext

@Composable
fun AvatarCreationScreen(onAvatarCreated: (Color) -> Unit) {
    var selectedColor by remember { mutableStateOf(Color.Transparent) }
    var hue by remember { mutableStateOf(0f) }
    var brightness by remember { mutableStateOf(0.5f) }

    val customColor = Color.hsv(hue, 1f, brightness)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar preview
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(selectedColor, CircleShape)
                .border(2.dp, Color.Black, CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Predefined color palette
        Text("Select a Predefined Color", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        ColorPalette(
            colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow),
            onColorSelected = { color ->
                selectedColor = color
                hue = 0f // Reset dello slider per Hue
                brightness = 1f // Reset dello slider per Brightness
                UserContext.avatar = color.hashCode()
                if (UserContext.isLoggedIn) {
                    UserContext.updateAvatar(color.hashCode())
                }
                onAvatarCreated(color)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Colors sliders
        Text("Customize Your Color", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        ColorSlider(
            label = "Hue",
            value = hue,
            onValueChange = { hue = it; selectedColor = customColor },
            valueRange = 0f..360f,
            colors = List(360) { i -> Color.hsv(i.toFloat(), 1f, 1f) }
        )

        ColorSlider(
            label = "Brightness",
            value = brightness,
            onValueChange = { brightness = it; selectedColor = customColor },
            valueRange = 0f..1f,
            colors = listOf(Color.Black, Color.hsv(hue, 1f, 1f))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirmation button
        Button(
            onClick = {
                UserContext.avatar = selectedColor.hashCode()
                if (UserContext.isLoggedIn) {
                    UserContext.updateAvatar(selectedColor.hashCode())
                }
                onAvatarCreated(selectedColor)
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("Done")
        }
    }
}

@Composable
private fun ColorPalette(colors: List<Color>, onColorSelected: (Color) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        colors.forEach { color ->
            ColorButton(color = color, onClick = { onColorSelected(color) })
        }
    }
}

@Composable
private fun ColorButton(color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(color, CircleShape)
            .border(2.dp, Color.Black, CircleShape)
            .clickable { onClick() }
    )
}

@Composable
private fun ColorSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(Brush.horizontalGradient(colors))
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = colors.lastOrNull() ?: Color.White
            )
        )
    }
}
