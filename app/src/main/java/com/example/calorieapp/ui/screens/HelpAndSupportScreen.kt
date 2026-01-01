package com.example.calorieapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HelpAndSupportScreen(
    onBackClick: () -> Unit,
    onContactClick: () -> Unit
) {
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text("←", fontSize = 24.sp)
            }
            Text(
                "Help & Support",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                Text("Frequently Asked Questions", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
            }

            // FAQ Items
            item { FaqItem("How is my calorie goal calculated?", "Your daily calorie goal is calculated using the Harris-Benedict equation, which takes into account your age, weight, height, and gender. Your selected fitness goal (lose, maintain, or gain weight) adjusts this number accordingly.") }
            item { FaqItem("How do I add a new meal?", "You can add a new meal by navigating to the 'Add Meal' tab from the bottom navigation bar or by tapping the floating action button on the home screen.") }
            item { FaqItem("Can I edit a meal I've already logged?", "Currently, you can delete a meal from the 'Reports' screen. Editing logged meals is a feature we are working on!") }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text("Contact Us", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onContactClick)
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Email Support", fontSize = 16.sp)
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
                HorizontalDivider(color = Color.LightGray)
            }
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(question, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(answer, fontSize = 14.sp, color = Color.Gray)
        HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
    }
}
