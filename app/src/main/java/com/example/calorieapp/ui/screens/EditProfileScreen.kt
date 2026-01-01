package com.example.calorieapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    name: String,
    email: String,
    age: Int,
    weight: Double,
    height: Double,
    goal: String,
    onBackClick: () -> Unit,
    onSaveClick: (String, Int, Double, Double, String) -> Unit
) {
    var newName by remember { mutableStateOf(name) }
    var newAge by remember { mutableStateOf(age.toString()) }
    var newWeight by remember { mutableStateOf(weight.toString()) }
    var newHeight by remember { mutableStateOf(height.toString()) }
    var newGoal by remember { mutableStateOf(goal) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onBackground)
            ) {
                Text("←", fontSize = 24.sp)
            }
            Text("Edit Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Picture
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { /* TODO: Handle profile picture change */ },
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 50.sp) // Placeholder Emoji
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
                Text("Edit", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Personal Information Section
        Text("Personal Information", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
         OutlinedTextField(
            value = email,
            onValueChange = {},
            label = { Text("Email (cannot be changed)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = newAge,
                onValueChange = { newAge = it },
                label = { Text("Age") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = newWeight,
                onValueChange = { newWeight = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = newHeight,
                onValueChange = { newHeight = it },
                label = { Text("Height (cm)") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Goal Selector
        val goals = listOf("Lose Weight", "Maintain Weight", "Gain Weight")
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = newGoal,
                onValueChange = {},
                readOnly = true,
                label = { Text("Goal") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                goals.forEach { goal ->
                    DropdownMenuItem(
                        text = { Text(goal) },
                        onClick = {
                            newGoal = goal
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { 
                onSaveClick(
                    newName,
                    newAge.toIntOrNull() ?: age,
                    newWeight.toDoubleOrNull() ?: weight,
                    newHeight.toDoubleOrNull() ?: height,
                    newGoal
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Save Changes", fontSize = 18.sp)
        }
    }
}
