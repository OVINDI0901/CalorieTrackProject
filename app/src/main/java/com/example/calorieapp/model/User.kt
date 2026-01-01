package com.example.calorieapp.model

data class User(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val gender: String = "Male",
    val goal: String = "Maintain Weight",
    val calorieGoal: Int = 2000
)