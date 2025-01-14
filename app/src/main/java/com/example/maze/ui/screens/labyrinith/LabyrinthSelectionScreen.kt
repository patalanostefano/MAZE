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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.maze.data.model.Labyrinth
import com.example.maze.data.network.FirebaseService
import com.example.maze.data.repository.LabyrinthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun LabyrinthSelectorScreen(
    viewModel: LabyrinthSelectorViewModel = viewModel(),
    onLabyrinthSelected: (String) -> Unit
) {
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
                    decodeBase64ToBitmap(labyrinth.fullImageUrl)
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

class LabyrinthSelectorViewModel(
    private val repository: LabyrinthRepository = LabyrinthRepository(FirebaseService())
) : ViewModel() {
    private val _labyrinths = MutableStateFlow<List<Labyrinth>>(emptyList())
    val labyrinths: StateFlow<List<Labyrinth>> = _labyrinths.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadLabyrinths()
    }

    private fun loadLabyrinths() {
        viewModelScope.launch {
            _isLoading.value = true
            _labyrinths.value = repository.getAllLabyrinths()
            _isLoading.value = false
        }
    }
}

private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}
