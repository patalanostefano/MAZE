// data/model/Labyrinth.kt
package com.example.maze.data.model

data class Labyrinth(
    val id: String = "",
    val name: String = "",
    val structure: List<List<Int>> = emptyList(),
    val entrance: List<Int> = listOf(1, 1),
    val exit: List<Int> = listOf(29, 29),
    val fullImageUrl: String = ""
) {
    companion object {
        fun fromFirestore(data: Map<String, Any?>): Labyrinth {
            return Labyrinth(
                id = data["id"] as? String ?: "",
                name = data["name"] as? String ?: "",
                structure = parseStructure(data["structure"]),
                entrance = parseCoordinates(data["entrance"]),
                exit = parseCoordinates(data["exit"]),
                fullImageUrl = data["fullImageUrl"] as? String ?: ""
            )
        }

        private fun parseStructure(structureData: Any?): List<List<Int>> {
            return when (structureData) {
                is Map<*, *> -> {
                    // Sort by key to maintain order
                    val sortedRows = structureData.entries.sortedBy {
                        (it.key as? String)?.toIntOrNull() ?: -1
                    }

                    sortedRows.map { (_, rowData) ->
                        when (rowData) {
                            is Map<*, *> -> {
                                // Sort the inner map by key as well
                                rowData.entries.sortedBy {
                                    (it.key as? String)?.toIntOrNull() ?: -1
                                }.map { (_, value) ->
                                    when (value) {
                                        is Long -> value.toInt()
                                        is Int -> value
                                        else -> 0 // Default to wall if invalid
                                    }
                                }
                            }
                            is List<*> -> rowData.map {
                                when (it) {
                                    is Long -> it.toInt()
                                    is Int -> it
                                    else -> 0
                                }
                            }
                            else -> emptyList()
                        }
                    }
                }
                is List<*> -> structureData.map { row ->
                    when (row) {
                        is List<*> -> row.map {
                            when (it) {
                                is Long -> it.toInt()
                                is Int -> it
                                else -> 0
                            }
                        }
                        else -> emptyList()
                    }
                }
                else -> emptyList()
            }
        }


        private fun parseCoordinates(data: Any?): List<Int> {
            return when (data) {
                is List<*> -> data.mapNotNull { (it as? Long)?.toInt() }
                is Map<*, *> -> listOf(
                    ((data["0"] ?: data[0]) as? Long)?.toInt() ?: 29,
                    ((data["1"] ?: data[1]) as? Long)?.toInt() ?: 29
                )
                else -> listOf(29, 29)
            }
        }
    }
}
