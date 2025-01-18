package com.example.maze.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.maze.data.network.AuthService

class AuthViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val service = AuthService()
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}