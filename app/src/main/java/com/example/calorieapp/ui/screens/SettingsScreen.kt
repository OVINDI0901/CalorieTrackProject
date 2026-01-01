package com.example.calorieapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    name: String,
    email: String,
    useDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onBackground)) {
                Text("←", fontSize = 24.sp)
            }
            Text("Profile & Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Info
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.firstOrNull()?.toString()?.uppercase() ?: "U",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(name, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(email, color = Color.Gray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        // Theme Toggle
        SettingsToggleItem(title = "Dark Mode", isChecked = useDarkTheme, onCheckedChange = onThemeChange)

        // Settings Options
        SettingsItem("Edit Profile", onClick = onEditProfileClick)
        SettingsItem("Notifications", onClick = onNotificationsClick)
        SettingsItem("Privacy Settings", onClick = onPrivacyClick)
        SettingsItem("Help & Support", onClick = onHelpClick)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Red
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SettingsToggleItem(
    title: String, 
    isChecked: Boolean, 
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { onCheckedChange(!isChecked) }
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = if (enabled) MaterialTheme.colorScheme.onSurface else Color.Gray, fontSize = 16.sp)
            Switch(checked = isChecked, onCheckedChange = onCheckedChange, enabled = enabled)
        }
        HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)
    }
}

@Composable
fun SettingsItem(text: String, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon Placeholder
                Box(Modifier.size(24.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
            }
            Text(">", color = Color.Gray)
        }
        HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)
    }
}
