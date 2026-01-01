package com.example.calorieapp.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Meal(
    val id: String = "",
    val name: String = "",
    val calories: Int = 0,
    val date: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
    val userId: String = ""
)
