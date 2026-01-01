package com.example.calorieapp.model

import androidx.compose.ui.graphics.Color

data class MealItemData(
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val color: Color,
    val imageUrl: String
)
