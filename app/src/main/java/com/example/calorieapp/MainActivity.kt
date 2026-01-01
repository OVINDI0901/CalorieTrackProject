package com.example.calorieapp

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.calorieapp.model.MealItemData
import com.example.calorieapp.model.User
import com.example.calorieapp.ui.screens.*
import com.example.calorieapp.ui.theme.CalorieAppTheme
import com.example.calorieapp.ui.viewmodel.AuthViewModel
import com.example.calorieapp.ui.viewmodel.FirestoreViewModel
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel(), firestoreViewModel: FirestoreViewModel = viewModel()) {
    var useDarkTheme by remember { mutableStateOf(false) }
    var allNotificationsEnabled by remember { mutableStateOf(true) }
    var mealRemindersEnabled by remember { mutableStateOf(true) }
    var hydrationRemindersEnabled by remember { mutableStateOf(true) }
    var progressReportsEnabled by remember { mutableStateOf(true) }
    var snoozeUntil by remember { mutableStateOf<Long?>(null) }
    var quietHoursEnabled by remember { mutableStateOf(false) }
    var quietHoursStart by remember { mutableStateOf(LocalTime.of(22, 0)) } // 10 PM
    var quietHoursEnd by remember { mutableStateOf(LocalTime.of(8, 0)) } // 8 AM
    var isProfilePrivate by remember { mutableStateOf(false) }
    var analyticsEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current

    CalorieAppTheme(darkTheme = useDarkTheme) {
        val navController = rememberNavController()
        val startDestination = "onboarding"

        var showSplash by remember { mutableStateOf(true) }
        
        var userProfile by remember { mutableStateOf<User?>(null) }

        var currentCalories by remember { mutableStateOf(0) }
        var currentProtein by remember { mutableStateOf(0) }
        var currentCarbs by remember { mutableStateOf(0) }
        var currentFat by remember { mutableStateOf(0) }

        val addedMealItems = remember { mutableStateListOf<MealItemData>() }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        if (showSplash) {
            SplashScreen { showSplash = false }
        } else {
            Scaffold(
                bottomBar = {
                    if (currentRoute?.startsWith("home") == true || currentRoute == "add_meal" || currentRoute == "reports" || currentRoute?.startsWith("settings") == true || currentRoute == "edit_profile" || currentRoute == "notifications" || currentRoute == "privacy" || currentRoute == "help") {
                        BottomNavigationBar(
                            currentRoute = currentRoute,
                            onHomeClick = { userProfile?.let { navController.navigate("home/${it.name}") { launchSingleTop = true } } },
                            onAddMealClick = { navController.navigate("add_meal") },
                            onReportsClick = { navController.navigate("reports") },
                            onSettingsClick = { userProfile?.let { navController.navigate("settings/${it.name}") } }
                        )
                    }
                },
                floatingActionButton = {
                    if (currentRoute?.startsWith("home") == true) {
                        FloatingActionButton(onClick = { navController.navigate("add_meal") }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Meal")
                        }
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("onboarding") {
                            OnboardingScreen(onNextClick = { navController.navigate("login") })
                        }
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("loading") { popUpTo("onboarding") { inclusive = true } }
                                },
                                onSignUpClick = { navController.navigate("signup") },
                                onBackClick = { navController.popBackStack() },
                                onForgotPasswordClick = { navController.navigate("reset_password") }
                            )
                        }
                        composable("loading") {
                            LaunchedEffect(Unit) {
                                val profile = firestoreViewModel.loadUserProfile()
                                if (profile != null) {
                                    userProfile = profile
                                    navController.navigate("home/${profile.name}") { popUpTo("loading") { inclusive = true } }
                                } else {
                                    navController.navigate("profile_setup") { popUpTo("loading") { inclusive = true } }
                                }
                            }
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        composable("reset_password") {
                            ResetPasswordScreen(onBackClick = { navController.popBackStack() })
                        }
                        composable("signup") {
                            SignUpScreen(
                                onBackClick = { navController.popBackStack() },
                                onLoginClick = { navController.navigate("login") { popUpTo("signup") { inclusive = true } } },
                                onSignUpSuccess = { navController.navigate("signup_success") }
                            )
                        }
                        composable("signup_success") {
                            SignUpSuccessScreen(
                                onContinueClick = { navController.navigate("login") { popUpTo("signup") { inclusive = true } } }
                            )
                        }
                        composable("profile_setup") {
                            ProfileSetupScreen(
                                onContinueClick = { user ->
                                    userProfile = user
                                    navController.navigate("home/${user.name}") { popUpTo("profile_setup") { inclusive = true } }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("home/{name}", arguments = listOf(navArgument("name") { type = NavType.StringType })) {
                            val name = it.arguments?.getString("name") ?: userProfile?.name ?: "User"
                            HomeScreen(
                                name = name,
                                onBackClick = { /* No back action from home */ },
                                onMealClick = { mealName -> navController.navigate("detail/$mealName") },
                                totalCalories = currentCalories,
                                totalProtein = currentProtein,
                                totalCarbs = currentCarbs,
                                totalFat = currentFat,
                                addedMeals = addedMealItems.toList(),
                                calorieGoal = userProfile?.calorieGoal ?: 2000
                            )
                        }
                        composable("add_meal") {
                            AddMealScreen(
                                onBackClick = { navController.popBackStack() },
                                onAddMeal = { c, p, carb, f, items ->
                                    currentCalories += c
                                    currentProtein += p
                                    currentCarbs += carb
                                    currentFat += f
                                    addedMealItems.addAll(items)
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("reports") {
                            ReportsScreen(
                                onBackClick = { navController.popBackStack() },
                                onDeleteMeal = { meal ->
                                    addedMealItems.remove(meal)
                                    currentCalories -= meal.calories
                                    currentProtein -= meal.protein
                                    currentCarbs -= meal.carbs
                                    currentFat -= meal.fat
                                },
                                currentCalories = currentCalories,
                                currentProtein = currentProtein,
                                currentCarbs = currentCarbs,
                                currentFat = currentFat,
                                addedMeals = addedMealItems.toList()
                            )
                        }
                        composable("settings/{name}", arguments = listOf(navArgument("name") { type = NavType.StringType })) {
                             val name = it.arguments?.getString("name") ?: userProfile?.name ?: "User"
                            SettingsScreen(
                                name = name,
                                email = authViewModel.getCurrentUser()?.email ?: "",
                                useDarkTheme = useDarkTheme,
                                onThemeChange = { useDarkTheme = it },
                                onBackClick = { navController.popBackStack() },
                                onLogoutClick = {
                                    authViewModel.logout()
                                    navController.navigate("login") { popUpTo(navController.graph.startDestinationId) { inclusive = true } }
                                },
                                onEditProfileClick = { navController.navigate("edit_profile") },
                                onNotificationsClick = { navController.navigate("notifications") },
                                onPrivacyClick = { navController.navigate("privacy") },
                                onHelpClick = { navController.navigate("help") }
                            )
                        }
                        composable("edit_profile") {
                            EditProfileScreen(
                                name = userProfile?.name ?: "",
                                email = authViewModel.getCurrentUser()?.email ?: "",
                                age = userProfile?.age ?: 0,
                                weight = userProfile?.weight ?: 0.0,
                                height = userProfile?.height ?: 0.0,
                                goal = userProfile?.goal ?: "Maintain Weight",
                                onBackClick = { navController.popBackStack() },
                                onSaveClick = { newName, newAge, newWeight, newHeight, newGoal ->
                                    // In a real app, you would also save the updated profile here
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("notifications") {
                            val snoozeStatus = snoozeUntil?.let {
                                val formatter = DateTimeFormatter.ofPattern("h:mm a").withZone(ZoneId.systemDefault())
                                "Until ${formatter.format(Instant.ofEpochMilli(it))}"
                            } ?: "Off"

                            val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

                            NotificationsScreen(
                                onBackClick = { navController.popBackStack() },
                                allNotificationsEnabled = allNotificationsEnabled,
                                onAllNotificationsChange = { allNotificationsEnabled = it },
                                mealRemindersEnabled = mealRemindersEnabled,
                                onMealRemindersChange = { mealRemindersEnabled = it },
                                hydrationRemindersEnabled = hydrationRemindersEnabled,
                                onHydrationRemindersChange = { hydrationRemindersEnabled = it },
                                progressReportsEnabled = progressReportsEnabled,
                                onProgressReportsChange = { progressReportsEnabled = it },
                                snoozeStatus = snoozeStatus,
                                onSnoozeSelected = { minutes ->
                                    snoozeUntil = if (minutes > 0) {
                                        System.currentTimeMillis() + minutes * 60 * 1000
                                    } else {
                                        null
                                    }
                                },
                                quietHoursEnabled = quietHoursEnabled,
                                onQuietHoursChange = { quietHoursEnabled = it },
                                quietHoursStart = quietHoursStart.format(timeFormatter),
                                quietHoursEnd = quietHoursEnd.format(timeFormatter),
                                onStartTimeClick = {
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute -> quietHoursStart = LocalTime.of(hour, minute) },
                                        quietHoursStart.hour,
                                        quietHoursStart.minute,
                                        false
                                    ).show()
                                },
                                onEndTimeClick = {
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute -> quietHoursEnd = LocalTime.of(hour, minute) },
                                        quietHoursEnd.hour,
                                        quietHoursEnd.minute,
                                        false
                                    ).show()
                                }
                            )
                        }
                        composable("privacy") {
                            PrivacyScreen(
                                onBackClick = { navController.popBackStack() },
                                onDeleteAccount = {
                                    authViewModel.logout()
                                    navController.navigate("login") { popUpTo(navController.graph.startDestinationId) { inclusive = true } }
                                },
                                isProfilePrivate = isProfilePrivate,
                                onProfileVisibilityChange = { isProfilePrivate = it },
                                analyticsEnabled = analyticsEnabled,
                                onAnalyticsChange = { analyticsEnabled = it },
                                onPrivacyPolicyClick = { navController.navigate("detail/Privacy Policy") },
                                onTermsOfServiceClick = { navController.navigate("detail/Terms of Service") }
                            )
                        }
                        composable("help") {
                            HelpAndSupportScreen(
                                onBackClick = { navController.popBackStack() },
                                onContactClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:support@calorieapp.com")
                                        putExtra(Intent.EXTRA_SUBJECT, "Calorie App Support Request")
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                        composable("detail/{title}", arguments = listOf(navArgument("title") { type = NavType.StringType })) {
                            val title = it.arguments?.getString("title") ?: "Details"
                            DetailScreen(title = title, onBackClick = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onAddMealClick: () -> Unit,
    onReportsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute?.startsWith("home") == true,
            onClick = onHomeClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Add, contentDescription = "Add Meal") },
            label = { Text("Add") },
            selected = currentRoute == "add_meal",
            onClick = onAddMealClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.List, contentDescription = "Reports") },
            label = { Text("Reports") },
            selected = currentRoute == "reports",
            onClick = onReportsClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentRoute?.startsWith("settings") == true,
            onClick = onSettingsClick
        )
    }
}
