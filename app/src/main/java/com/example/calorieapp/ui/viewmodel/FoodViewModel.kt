package com.example.calorieapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.model.Meal
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class FoodViewModel : ViewModel() {

    private val db = Firebase.firestore

    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            db.collection("meals").add(meal)
        }
    }
}
