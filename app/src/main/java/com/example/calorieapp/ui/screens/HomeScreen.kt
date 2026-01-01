package com.example.calorieapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.model.MealItemData

@Composable
fun HomeScreen(
    name: String,
    onBackClick: () -> Unit,
    onMealClick: (String) -> Unit,
    totalCalories: Int,
    totalProtein: Int,
    totalCarbs: Int,
    totalFat: Int,
    addedMeals: List<MealItemData>,
    calorieGoal: Int = 2000 // A daily goal
) {
    val progress = (totalCalories.toFloat() / calorieGoal.toFloat()).coerceIn(0f, 1f)
    val remainingCalories = calorieGoal - totalCalories

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onBackground)) {
                    Text("←", fontSize = 24.sp)
                }
                Column {
                    Text("Welcome, $name!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }
            }
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("\uD83D\uDE4B\u200D♂️\u200B", fontSize = 35.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Calories Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp), // Increased height for the graph
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background track for the progress indicator
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(300.dp),
                    color = Color.White.copy(alpha = 0.3f),
                    strokeWidth = 12.dp,
                    trackColor = Color.Transparent
                )

                // Actual progress indicator
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(220.dp),
                    color = Color.White,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round,
                    trackColor = Color.Transparent
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Today\'s Calories", color = Color.White.copy(alpha = 0.8f))
                    Text(
                        "$totalCalories kcal",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Goal: $calorieGoal kcal",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        "Remaining: $remainingCalories kcal",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Macros Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MacroItem("Protein", "${totalProtein}g", MaterialTheme.colorScheme.onSurface)
                MacroItem("Carbs", "${totalCarbs}g", MaterialTheme.colorScheme.onSurface)
                MacroItem("Fat", "${totalFat}g", MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Breakdown by Meal
        Text("Breakdown by Meal", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (addedMeals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No meals added yet for today. Start by adding a meal!",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(addedMeals) { meal ->
                    MealBreakdownItem(meal = meal, onClick = { onMealClick(meal.name) })
                }
            }
        }
    }
}

@Composable
fun MacroItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = color.copy(alpha = 0.8f), fontSize = 12.sp)
    }
}

@Composable
fun MealBreakdownItem(meal: MealItemData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(meal.name, fontWeight = FontWeight.Bold)
                Text(
                    "P: ${meal.protein}g, C: ${meal.carbs}g, F: ${meal.fat}g",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text("${meal.calories} kcal", fontWeight = FontWeight.Bold)
        }
    }
}
