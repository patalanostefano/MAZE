package com.example.maze.ui.screens.labyrinth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.maze.data.model.Labyrinth
import com.example.maze.utils.ImageUtils


@Composable
fun LabyrinthSelectorScreen(
    viewModel: LabyrinthSelectorViewModel = viewModel(
        factory = LabyrinthSelectorViewModelFactory()
    ),
    onLabyrinthSelected: (String) -> Unit) {
    val labyrinths by viewModel.labyrinths.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select a Labyrinth",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(labyrinths) { labyrinth ->
                    LabyrinthItem(
                        labyrinth = labyrinth,
                        onClick = { onLabyrinthSelected(labyrinth.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LabyrinthItem(
    labyrinth: Labyrinth,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = labyrinth.name,
                style = MaterialTheme.typography.titleLarge
            )

            if (labyrinth.fullImageUrl.isNotEmpty()) {
                val bitmap = remember(labyrinth.fullImageUrl) {
                    ImageUtils.decodeBase64ToBitmap(labyrinth.fullImageUrl)
                }

                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Labyrinth preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(top = 8.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

