package com.example.maze.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maze.data.model.UserContext

@Composable
fun AuthPage(
    onLogin: () -> Unit,
    getAvatar: () -> Unit,
    viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory()
    )
) {
    var username by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    val errorState by viewModel.errorState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() } 

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onLogin()
        }
    }

    LaunchedEffect(errorState) { //Show snackbar to show error
        errorState?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.clearError()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //User inputs name here
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Enter username") },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.7f)
        )

        //When name input is done, user logs in
        Button(
            onClick = { viewModel.login(username, rememberMe) },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.7f)
        ) {
            Text("Log in")
        }

        Button(
            onClick = {
                getAvatar()
                UserContext.avatar?.let { viewModel.register(username, it) }
            }, //Register and login
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.7f)
        ) {
            Text("Register")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = {
                    rememberMe = it
                }
            )

            Text("Remember me?")
        }
    }
    SnackbarHost(hostState = snackbarHostState)
}

@Preview(name = "User Login screen", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun AuthScreenPreview() {
    AuthPage(
        onLogin = {},
        getAvatar = {}
    )
}