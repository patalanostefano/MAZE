package com.example.maze.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AuthPage(
    onLogin: (String) -> Unit,
    onRegister: (String) -> Unit,
    setRememberMe: (Boolean) -> Unit,
    saveSession: suspend (Boolean, String) -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var registrationStatus by remember { mutableStateOf(false) }
    var loginStatus by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //User inputs name here
        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Enter username") },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.7f)
        )

        //When name input is done, user logs in
        Button(
            onClick = { println(userName); onLogin(userName) },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.7f)
        ) {
            Text("Log in")
        }

        Button(
            onClick = { onRegister(userName); onLogin(userName) }, //Register and login
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.7f)
        ) {
            Text("Register & Log in")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = {
                    rememberMe = it
                    setRememberMe(it)

                }
            )

            Text("Remember me?")
        }
    }
}

@Preview(name = "User Login screen", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun AuthScreenPreview() {
    AuthPage(
        onLogin = { /* No-op */ },
        onRegister = { /* No-op */ },
        setRememberMe = { /* No-op */ },
        saveSession = { _, _ -> /* No-op */ }
    )
}