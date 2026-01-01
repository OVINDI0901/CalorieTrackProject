package com.example.calorieapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calorieapp.model.User
import com.example.calorieapp.ui.viewmodel.AuthViewModel
import com.example.calorieapp.ui.viewmodel.FirestoreViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onContinueClick: (User) -> Unit, 
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    firestoreViewModel: FirestoreViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") }
    var primaryGoal by remember { mutableStateOf("Maintain Weight") }
    val goalOptions = listOf("Lose Weight", "Maintain Weight", "Gain Weight")
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
             Button(onClick = onBackClick, enabled = !isLoading, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Black)) {
                Text("←", fontSize = 24.sp)
            }
            Text("Complete Your Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(15.dp))
        
        LinearProgressIndicator(
            progress = { 0.5f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "\uD83E\uDEC6\u200B",
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Column(modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Enter Your Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GenderOption("Male", selectedGender == "Male") { selectedGender = "Male" }
                GenderOption("Female", selectedGender == "Female") { selectedGender = "Female" }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height (cm)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Primary Goal", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            goalOptions.forEach { goal ->
                GoalRadioButton(
                    text = goal,
                    selected = primaryGoal == goal,
                    onClick = { primaryGoal = goal }
                )
            }
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
        }

        Button(
            onClick = { 
                scope.launch {
                    isLoading = true
                    error = null
                    try {
                        val ageInt = age.toIntOrNull() ?: 0
                        val weightDouble = weight.toDoubleOrNull() ?: 0.0
                        val heightDouble = height.toDoubleOrNull() ?: 0.0

                        val bmr = if (selectedGender == "Male") {
                            88.362 + (13.397 * weightDouble) + (4.799 * heightDouble) - (5.677 * ageInt)
                        } else {
                            447.593 + (9.247 * weightDouble) + (3.098 * heightDouble) - (4.330 * ageInt)
                        }
                        val finalCalorieGoal = when (primaryGoal) {
                            "Lose Weight" -> (bmr * 0.8).toInt()
                            "Gain Weight" -> (bmr * 1.2).toInt()
                            else -> bmr.toInt()
                        }

                        val user = User(
                            id = authViewModel.getCurrentUser()?.uid ?: "",
                            name = name,
                            age = ageInt,
                            weight = weightDouble,
                            height = heightDouble,
                            gender = selectedGender,
                            goal = primaryGoal,
                            calorieGoal = finalCalorieGoal
                        )
                        firestoreViewModel.saveUserProfile(user)
                        onContinueClick(user)
                    } catch (e: Exception) {
                        error = "Could not save profile. Please try again."
                    } finally {
                        isLoading = false
                    }
                }
             },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Continue", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun GenderOption(gender: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.LightGray, shape = RoundedCornerShape(30.dp)),
             contentAlignment = Alignment.Center
        ) {
             Text(gender.first().toString(), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(gender, color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray)
    }
}

@Composable
fun GoalRadioButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}