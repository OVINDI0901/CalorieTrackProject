package com.example.calorieapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.calorieapp.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun saveUserProfile(user: User) {
        val currentUser = auth.currentUser ?: return
        // In a real app, you would handle potential errors here too
        db.collection("users").document(currentUser.uid).set(user).await()
    }

    suspend fun loadUserProfile(): User? {
        val currentUser = auth.currentUser ?: return null
        return try {
            val snapshot = db.collection("users").document(currentUser.uid).get().await()
            snapshot.toObject<User>()
        } catch (e: Exception) {
            // If there's any error (e.g., network issue, permissions), return null instead of crashing
            null
        }
    }
}
