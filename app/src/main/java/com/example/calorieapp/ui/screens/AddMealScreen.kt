package com.example.calorieapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.model.MealItemData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    onBackClick: () -> Unit,
    onAddMeal: (Int, Int, Int, Int, List<MealItemData>) -> Unit // calories, protein, carbs, fat, items
) {
    var selectedMealType by remember { mutableStateOf("Breakfast") }
    var showDialog by remember { mutableStateOf(false) }
    var editingMeal by remember { mutableStateOf<MealItemData?>(null) }

    // Sample Data with Macros (Protein, Carbs, Fat) - now mutable
    val breakfastItems = remember { mutableStateListOf(
        MealItemData("Banana Meat", 520, 30, 45, 10, Color(0xFFE57373), "https://example.com/banana_meat.jpg"),
        MealItemData("Greek-Fast", 455, 25, 30, 8, Color(0xFF81C784), "https://example.com/greek_fast.jpg"),
        MealItemData("Toast Carrot", 269, 5, 50, 2, Color(0xFFFFB74D), "https://example.com/toast_carrot.jpg"),
        MealItemData("Oatmeal & Fruits", 250, 8, 45, 4, Color(0xFFFFCC80), "https://example.com/oatmeal_fruits.jpg")
    ) }
    
    val lunchItems = remember { mutableStateListOf(
        MealItemData("Chicken Salad", 400, 40, 10, 15, Color(0xFF64B5F6), "https://example.com/chicken_salad.jpg"),
        MealItemData("Rice & Beans", 350, 12, 60, 5, Color(0xFFBA68C8), "https://example.com/rice_beans.jpg"),
        MealItemData("Grilled Chicken Salad", 320, 39, 12, 10, Color(0xFF81D4FA), "https://example.com/grilled_chicken_salad.jpg")
    ) }

    val dinnerItems = remember { mutableStateListOf(
        MealItemData("Steak", 600, 50, 0, 40, Color(0xFFA1887F), "https://example.com/steak.jpg"),
        MealItemData("Soup", 200, 5, 20, 8, Color(0xFF90A4AE), "https://example.com/soup.jpg"),
        MealItemData("Salmon & Veggies", 450, 40, 10, 25, Color(0xFFFFAB91), "https://example.com/salmon_veggies.jpg")
    ) }

    // State for selected items (Set of names)
    val selectedItems = remember { mutableStateListOf<String>() }

    val currentItems = when(selectedMealType) {
        "Breakfast" -> breakfastItems
        "Lunch" -> lunchItems
        "Dinner" -> dinnerItems
        else -> breakfastItems
    }

    // Calculate totals based on selection
    val selectedMealItems = currentItems.filter { selectedItems.contains(it.name) }
    val totalCalories = selectedMealItems.sumOf { it.calories }
    val totalProtein = selectedMealItems.sumOf { it.protein }
    val totalCarbs = selectedMealItems.sumOf { it.carbs }
    val totalFat = selectedMealItems.sumOf { it.fat }

    if (showDialog) {
        AddMealDialog(
            mealToEdit = editingMeal,
            onDismiss = { 
                showDialog = false
                editingMeal = null
            },
            onConfirm = { newMeal ->
                if (editingMeal != null) {
                    val index = currentItems.indexOf(editingMeal!!)
                    if (index != -1) {
                        currentItems[index] = newMeal
                    }
                } else {
                    currentItems.add(newMeal)
                }
                showDialog = false
                editingMeal = null
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add New Meal")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
                .padding(16.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onBackground)) {
                    Text("←", fontSize = 24.sp)
                }
                Text("Add Your Meal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Meal Type Selector
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp)).padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MealTypeButton("Breakfast", selectedMealType == "Breakfast") { 
                    selectedMealType = "Breakfast"
                    selectedItems.clear()
                }
                MealTypeButton("Lunch", selectedMealType == "Lunch") { 
                    selectedMealType = "Lunch" 
                    selectedItems.clear()
                }
                MealTypeButton("Dinner", selectedMealType == "Dinner") { 
                    selectedMealType = "Dinner" 
                    selectedItems.clear()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // List of items
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(currentItems) { index, item ->
                    MealItem(
                        item = item,
                        isSelected = selectedItems.contains(item.name),
                        onClick = {
                            if (selectedItems.contains(item.name)) {
                                selectedItems.remove(item.name)
                            } else {
                                selectedItems.add(item.name)
                            }
                        },
                        onEdit = {
                            editingMeal = item
                            showDialog = true
                        },
                        onDelete = { 
                            currentItems.removeAt(index) 
                        }
                    )
                }
            }
            
            // Total and Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                 Text("Total Calories", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                 Text("$totalCalories kcal", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { 
                    onAddMeal(totalCalories, totalProtein, totalCarbs, totalFat, selectedMealItems)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                 shape = RoundedCornerShape(8.dp),
                 enabled = totalCalories > 0
            ) {
                Text("Add Meal", color = Color.White)
            }
        }
    }
}

@Composable
private fun AddMealDialog(mealToEdit: MealItemData?, onDismiss: () -> Unit, onConfirm: (MealItemData) -> Unit) {
    var name by remember { mutableStateOf(mealToEdit?.name ?: "") }
    var calories by remember { mutableStateOf(mealToEdit?.calories?.toString() ?: "") }
    var protein by remember { mutableStateOf(mealToEdit?.protein?.toString() ?: "") }
    var carbs by remember { mutableStateOf(mealToEdit?.carbs?.toString() ?: "") }
    var fat by remember { mutableStateOf(mealToEdit?.fat?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (mealToEdit == null) "Add a New Meal" else "Edit Meal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Meal Name") })
                TextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories") })
                TextField(value = protein, onValueChange = { protein = it }, label = { Text("Protein (g)") })
                TextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbs (g)") })
                TextField(value = fat, onValueChange = { fat = it }, label = { Text("Fat (g)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newMeal = MealItemData(
                        name = name,
                        calories = calories.toIntOrNull() ?: 0,
                        protein = protein.toIntOrNull() ?: 0,
                        carbs = carbs.toIntOrNull() ?: 0,
                        fat = fat.toIntOrNull() ?: 0,
                        color = mealToEdit?.color ?: Color.Gray, // Retain original color or use default
                        imageUrl = mealToEdit?.imageUrl ?: ""
                    )
                    onConfirm(newMeal)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MealTypeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Gray
        ),
        elevation = null
    ) {
        Text(text)
    }
}

@Composable
fun MealItem(
    item: MealItemData, 
    isSelected: Boolean, 
    onClick: () -> Unit, 
    onEdit: () -> Unit, 
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Icon placeholder / Image
                Box(Modifier.size(40.dp).background(item.color, RoundedCornerShape(8.dp)))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(item.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = "P: ${item.protein}g  C: ${item.carbs}g  F: ${item.fat}g", 
                        fontSize = 12.sp, 
                        color = Color.Gray
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Meal", tint = MaterialTheme.colorScheme.onSurface)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Meal", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
