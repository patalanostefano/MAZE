package com.example.maze.data.model

// data/model/Labyrinth.kt
data class Labyrinth(
    val id: String = "",
    val name: String = "",
    val dimensions: Dimensions = Dimensions(),
    val structure: Map<String, Map<String, Int>> = emptyMap(),  // Firestore format
    val startPosition: Position = Position(),
    val endPosition: Position = Position(),
    val fullImageUrl: String = "",  // Base64 encoded image
    val imageFormat: String = ""
) {
    fun getStructureArray(): List<List<Int>> {
        return structure.keys.sortedBy { it.toInt() }.map { rowKey ->
            val row = structure[rowKey] ?: emptyMap()
            row.keys.sortedBy { it.toInt() }.map { colKey ->
                row[colKey] ?: 0
            }
        }
    }
}

data class Dimensions(
    val width: Int = 0,
    val height: Int = 0
)

data class Position(
    val x: Int = 0,
    val y: Int = 0
)
