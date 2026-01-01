package com.example.calorieapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    allNotificationsEnabled: Boolean,
    onAllNotificationsChange: (Boolean) -> Unit,
    mealRemindersEnabled: Boolean,
    onMealRemindersChange: (Boolean) -> Unit,
    hydrationRemindersEnabled: Boolean,
    onHydrationRemindersChange: (Boolean) -> Unit,
    progressReportsEnabled: Boolean,
    onProgressReportsChange: (Boolean) -> Unit,
    snoozeStatus: String,
    onSnoozeSelected: (Int) -> Unit, // Duration in minutes
    quietHoursEnabled: Boolean,
    onQuietHoursChange: (Boolean) -> Unit,
    quietHoursStart: String,
    quietHoursEnd: String,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val snoozeOptions = mapOf(
        "Off" to 0,
        "1 Hour" to 60,
        "3 Hours" to 180,
        "Until Tomorrow" to 1440
    )

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
                "Notifications",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Main Toggle
        SettingsToggleItem(
            title = "All Notifications",
            isChecked = allNotificationsEnabled,
            onCheckedChange = onAllNotificationsChange
        )

        // Granular Toggles
        SettingsToggleItem(
            title = "Meal Reminders",
            isChecked = mealRemindersEnabled && allNotificationsEnabled,
            onCheckedChange = onMealRemindersChange,
            enabled = allNotificationsEnabled
        )
        SettingsToggleItem(
            title = "Hydration Reminders",
            isChecked = hydrationRemindersEnabled && allNotificationsEnabled,
            onCheckedChange = onHydrationRemindersChange,
            enabled = allNotificationsEnabled
        )
        SettingsToggleItem(
            title = "Progress Reports",
            isChecked = progressReportsEnabled && allNotificationsEnabled,
            onCheckedChange = onProgressReportsChange,
            enabled = allNotificationsEnabled
        )

        HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

        // Snooze Options
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = allNotificationsEnabled) { expanded = true }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Snooze Notifications", fontSize = 16.sp, color = if(allNotificationsEnabled) MaterialTheme.colorScheme.onBackground else Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(snoozeStatus, color = if(allNotificationsEnabled) MaterialTheme.colorScheme.primary else Color.Gray, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Snooze options", tint = if(allNotificationsEnabled) Color.Gray else Color.Transparent)
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                snoozeOptions.forEach { (text, duration) ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            onSnoozeSelected(duration)
                            expanded = false
                        }
                    )
                }
            }
        }
        
        HorizontalDivider()

        // Quiet Hours
        SettingsToggleItem(
            title = "Quiet Hours",
            isChecked = quietHoursEnabled && allNotificationsEnabled,
            onCheckedChange = onQuietHoursChange,
            enabled = allNotificationsEnabled
        )

        if (quietHoursEnabled && allNotificationsEnabled) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text("From:")
                TextButton(onClick = onStartTimeClick) {
                    Text(quietHoursStart, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Text("To:")
                TextButton(onClick = onEndTimeClick) {
                    Text(quietHoursEnd, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        HorizontalDivider()
    }
}
