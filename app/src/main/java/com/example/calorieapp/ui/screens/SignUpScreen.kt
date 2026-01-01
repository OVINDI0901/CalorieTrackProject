package com.example.calorieapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calorieapp.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSignUpSuccess: () -> Unit, // Added this
    authViewModel: AuthViewModel = viewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMismatchError by remember { mutableStateOf(false) }
    var signUpError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onBackClick, 
                enabled = !isLoading, 
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Black)
            ) {
                Text("←", fontSize = 24.sp)
            }
            Text("Sign Up", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.weight(0.8f))
        Text(
            text = "\uD83E\uDD51",
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.weight(0.01f))

        // Sign Up Form
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Create Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            isError = passwordMismatchError,
            enabled = !isLoading
        )

        if (passwordMismatchError) {
            Text("Passwords do not match", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top=8.dp))
        }
        signUpError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top=8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (password != confirmPassword) {
                    passwordMismatchError = true
                    return@Button
                }
                passwordMismatchError = false
                signUpError = null
                
                scope.launch {
                    isLoading = true
                    val success = authViewModel.signUp(email, password)
                    if (success) {
                        onSignUpSuccess() // Changed this
                    } else {
                        signUpError = "Sign-up failed. Email may be taken or invalid."
                    }
                    isLoading = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading && firstName.isNotBlank() && email.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Sign Up", fontSize = 18.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onLoginClick, enabled = !isLoading) {
            Text("Already you have and account? login", color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
