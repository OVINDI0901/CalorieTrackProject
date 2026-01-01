package com.example.calorieapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.calorieapp.model.MealItemData
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBackClick: () -> Unit,
    onDeleteMeal: (MealItemData) -> Unit,
    currentCalories: Int = 0,
    currentProtein: Int = 0,
    currentCarbs: Int = 0,
    currentFat: Int = 0,
    addedMeals: List<MealItemData> = emptyList()
) {
    var selectedRange by remember { mutableStateOf("Day") }
    var selectedNutrient by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Mock Data based on Range and Actual Current Stats
    val (protein, carbs, fat) = when (selectedRange) {
        "Day" -> Triple("${currentProtein}g", "${currentCarbs}g", "${currentFat}g")
        "Week" -> Triple("${currentProtein * 7}g", "${currentCarbs * 7}g", "${currentFat * 7}g")
        "Month" -> Triple("${currentProtein * 30}g", "${currentCarbs * 30}g", "${currentFat * 30}g")
        "Year" -> Triple("${currentProtein * 365}g", "${currentCarbs * 365}g", "${currentFat * 365}g")
        else -> Triple("0g", "0g", "0g")
    }

    // Mock Graph Data for each nutrient
    val caloriesData = mapOf(
        "Day" to listOf(0.1f, 0.6f, 0.8f, 0.4f), // 4 bars for 6-hour intervals
        "Week" to listOf(0.5f, 0.7f, 0.4f, 0.9f, 0.6f, 0.8f, 0.5f),
        "Month" to listOf(0.6f, 0.5f, 0.8f, 0.7f),
        "Year" to listOf(0.4f, 0.6f, 0.5f, 0.7f, 0.8f, 0.6f, 0.5f, 0.7f, 0.6f, 0.5f, 0.7f, 0.8f)
    )
    val proteinData = mapOf(
        "Day" to listOf(0.2f, 0.7f, 0.9f, 0.5f),
        "Week" to listOf(0.6f, 0.8f, 0.5f, 0.9f, 0.7f, 0.8f, 0.6f),
        "Month" to listOf(0.7f, 0.6f, 0.9f, 0.8f),
        "Year" to listOf(0.5f, 0.7f, 0.6f, 0.8f, 0.9f, 0.7f, 0.6f, 0.8f, 0.7f, 0.6f, 0.8f, 0.9f)
    )
    val carbsData = mapOf(
        "Day" to listOf(0.3f, 0.5f, 0.9f, 0.6f),
        "Week" to listOf(0.9f, 0.8f, 0.7f, 0.8f, 0.6f, 0.7f, 0.9f),
        "Month" to listOf(0.8f, 0.9f, 0.7f, 0.6f),
        "Year" to listOf(0.9f, 0.8f, 0.7f, 0.6f, 0.7f, 0.8f, 0.9f, 0.7f, 0.8f, 0.9f, 0.7f, 0.8f)
    )
    val fatData = mapOf(
        "Day" to listOf(0.1f, 0.3f, 0.5f, 0.2f),
        "Week" to listOf(0.4f, 0.3f, 0.5f, 0.4f, 0.6f, 0.3f, 0.4f),
        "Month" to listOf(0.5f, 0.4f, 0.6f, 0.5f),
        "Year" to listOf(0.3f, 0.4f, 0.5f, 0.6f, 0.5f, 0.4f, 0.3f, 0.5f, 0.4f, 0.5f, 0.6f, 0.5f)
    )

    val graphData = when (selectedNutrient) {
        "Protein" -> proteinData[selectedRange] ?: emptyList()
        "Carbs" -> carbsData[selectedRange] ?: emptyList()
        "Fat" -> fatData[selectedRange] ?: emptyList()
        else -> caloriesData[selectedRange] ?: emptyList()
    }
    
    val graphColor = when (selectedNutrient) {
        "Protein" -> MaterialTheme.colorScheme.primary
        "Carbs" -> Color(0xFFFFA726)
        "Fat" -> Color(0xFFEF5350)
        else -> MaterialTheme.colorScheme.primary
    }

    val mealsToShow = remember(addedMeals) {
        addedMeals.ifEmpty { emptyList() }
    }

    val dayFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val yearFormatter = DateTimeFormatter.ofPattern("yyyy")

    val dateText = when (selectedRange) {
        "Day" -> selectedDate.format(dayFormatter)
        "Week" -> {
            val startOfWeek = selectedDate.with(DayOfWeek.MONDAY)
            val endOfWeek = startOfWeek.plusDays(6)
            "${startOfWeek.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${endOfWeek.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}"
        }
        "Month" -> selectedDate.format(monthFormatter)
        "Year" -> selectedDate.format(yearFormatter)
        else -> ""
    }

    val onPreviousClick = {
        selectedDate = when (selectedRange) {
            "Day" -> selectedDate.minusDays(1)
            "Week" -> selectedDate.minusWeeks(1)
            "Month" -> selectedDate.minusMonths(1)
            "Year" -> selectedDate.minusYears(1)
            else -> selectedDate
        }
    }

    val onNextClick = {
        selectedDate = when (selectedRange) {
            "Day" -> selectedDate.plusDays(1)
            "Week" -> selectedDate.plusWeeks(1)
            "Month" -> selectedDate.plusMonths(1)
            "Year" -> selectedDate.plusYears(1)
            else -> selectedDate
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onBackground)
            ) {
                Text("←", fontSize = 24.sp)
            }
            Text("Reports / History", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Time Range Tabs
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp)).padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TimeRangeButton("Day", selectedRange == "Day") { selectedRange = "Day" }
            TimeRangeButton("Week", selectedRange == "Week") { selectedRange = "Week" }
            TimeRangeButton("Month", selectedRange == "Month") { selectedRange = "Month" }
            TimeRangeButton("Year", selectedRange == "Year") { selectedRange = "Year" }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
            }
            Text(
                text = dateText,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable { showDatePicker = true }.padding(horizontal = 16.dp)
            )
            IconButton(onClick = onNextClick) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Graph
        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inversePrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(if (selectedNutrient != null) "$selectedNutrient Trend" else "Calories Trend", fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                SimpleBarGraph(data = graphData, color = graphColor)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Nutrients", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NutrientCard(
                label = "Protein",
                value = protein,
                color = MaterialTheme.colorScheme.primary,
                isSelected = selectedNutrient == "Protein",
                onClick = { selectedNutrient = if (selectedNutrient == "Protein") null else "Protein" }
            )
            NutrientCard(
                label = "Carbs",
                value = carbs,
                color = Color(0xFFFFA726),
                isSelected = selectedNutrient == "Carbs",
                onClick = { selectedNutrient = if (selectedNutrient == "Carbs") null else "Carbs" }
            )
            NutrientCard(
                label = "Fat",
                value = fat,
                color = Color(0xFFEF5350),
                isSelected = selectedNutrient == "Fat",
                onClick = { selectedNutrient = if (selectedNutrient == "Fat") null else "Fat" }
            )
        }

        // Details section
        Spacer(modifier = Modifier.height(24.dp))
        val detailTitle = selectedNutrient?.plus(" Details") ?: "Meal Details"
        Text(detailTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))

        // List of meals
        if (mealsToShow.isEmpty()) {
            Text("No meals added yet.", color = Color.Gray, modifier = Modifier.padding(8.dp))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(mealsToShow) { meal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = meal.imageUrl,
                                contentDescription = meal.name,
                                modifier = Modifier.size(40.dp).background(meal.color, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(meal.name, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                when (selectedNutrient) {
                                    "Protein" -> "${meal.protein}g"
                                    "Carbs" -> "${meal.carbs}g"
                                    "Fat" -> "${meal.fat}g"
                                    else -> "${meal.calories} kcal" // Default to calories if nothing selected
                                },
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(onClick = { onDeleteMeal(meal) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete meal",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            initialDisplayMode = DisplayMode.Picker
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun TimeRangeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        elevation = null,
        modifier = Modifier.height(36.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
    ) {
        Text(text, fontSize = 12.sp)
    }
}

@Composable
fun NutrientCard(label: String, value: String, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface),
        border = if (isSelected) BorderStroke(2.dp, color) else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SimpleBarGraph(data: List<Float>, color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (data.isNotEmpty()) {
            val width = size.width
            val height = size.height
            val barWidth = width / (data.size * 2)
            val space = width / (data.size + 1)

            data.forEachIndexed { index, value ->
                // Draw rounded rect or simple rect
                drawRect(
                    color = color,
                    topLeft = Offset(
                        x = space * (index + 1) - barWidth / 2,
                        y = height * (1 - value)
                    ),
                    size = Size(barWidth, height * value)
                )
            }
        }
    }
}
