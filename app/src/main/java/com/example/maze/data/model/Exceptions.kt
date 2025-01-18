package com.example.maze.data.model

class UserNotFoundException(message: String) : Exception(message)
class UserAlreadyExistsException(message: String) : Exception(message)