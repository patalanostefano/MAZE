package com.example.maze.ui.screens.labyrinth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maze.data.model.Labyrinth
import com.example.maze.data.repository.LabyrinthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ui/screens/labyrinth/LabyrinthSelectorViewModel.kt
class LabyrinthSelectorViewModel(
    private val repository: LabyrinthRepository
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