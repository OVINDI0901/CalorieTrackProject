package com.example.calorieapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyScreen(
    onBackClick: () -> Unit,
    onDeleteAccount: () -> Unit,
    isProfilePrivate: Boolean,
    onProfileVisibilityChange: (Boolean) -> Unit,
    analyticsEnabled: Boolean,
    onAnalyticsChange: (Boolean) -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                "Privacy Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Visibility
        Text("Profile Visibility", style = MaterialTheme.typography.titleMedium)
        SettingsToggleItem(
            title = "Private Profile",
            isChecked = isProfilePrivate,
            onCheckedChange = onProfileVisibilityChange
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 0.dp))
        
        // Data Usage
        Text("Data Usage", style = MaterialTheme.typography.titleMedium)
        SettingsToggleItem(
            title = "Share Anonymous Analytics",
            isChecked = analyticsEnabled,
            onCheckedChange = onAnalyticsChange
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 0.dp))

        // Legal
        Text("Legal", style = MaterialTheme.typography.titleMedium)
        PrivacyItem(text = "Privacy Policy", onClick = onPrivacyPolicyClick)
        PrivacyItem(text = "Terms of Service", onClick = onTermsOfServiceClick)

        HorizontalDivider(modifier = Modifier.padding(vertical = 0.dp))

        Text("Manage Account Data", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showDeleteDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("Delete Account", color = Color.Red)
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Account?") },
                text = { Text("Are you sure you want to permanently delete your account? All of your data will be removed. This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteAccount()
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun PrivacyItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, fontSize = 16.sp)
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = Color.Gray
        )
    }
    HorizontalDivider(color = Color.LightGray)
}
