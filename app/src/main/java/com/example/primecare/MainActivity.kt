package com.example.primecare

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.primecare.Home.HomeScreen
import com.example.primecare.Meals.MealsViewModel
import com.example.primecare.Meals.MealsViewModelFactory
import com.example.primecare.Meals.api.RetrofitClient
import com.example.primecare.Meals.ui.MealDetailScreen
import com.example.primecare.Meals.ui.MealsScreen
import com.example.primecare.Settings.SettingsScreen
import com.example.primecare.Statistics.StatisticsScreen
import com.example.primecare.WorkOut.ExerciseScreen
import com.example.primecare.WorkOut.ExerciseDetailScreen
import com.example.primecare.WorkOut.ExerciseViewModel
import com.example.primecare.components.EnhancedTabNavigation
import com.example.primecare.data.ThemePreferences
import com.example.primecare.ui.theme.PrimeCareTheme

class MainActivity : ComponentActivity() {
    private val themePreferences = ThemePreferences(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrimeCareTheme(themePreferences = themePreferences) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val mealsViewModel: MealsViewModel = viewModel(factory = MealsViewModelFactory(RetrofitClient.mealApiService))
    val exerciseViewModel: ExerciseViewModel = viewModel() // Initialize ExerciseViewModel
    var selectedTab by remember { mutableStateOf(1) } // Start on Meals tab
    val apiKey = BuildConfig.API_KEY

    Log.d("MainScreen", "API Key: $apiKey") // Debug API key

    if (apiKey.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("API Key is missing. Please configure it in local.properties.")
        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                EnhancedTabNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                        Log.d("MainScreen", "Navigating to tab: $index")
                        navController.navigate("main/$index") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "main/1"
            ) {
                composable("main/{tabIndex}") { backStackEntry ->
                    val tabIndex = backStackEntry.arguments?.getString("tabIndex")?.toIntOrNull() ?: 0
                    selectedTab = tabIndex
                    Log.d("MainScreen", "Rendering tab: $tabIndex")

                    when (tabIndex) {
                        0 -> HomeScreen(
                            modifier = Modifier.padding(innerPadding).fillMaxSize()
                        )
                        1 -> MealsScreen(
                            viewModel = mealsViewModel,
                            apiKey = apiKey,
                            onMealClick = { mealId ->
                                Log.d("MainScreen", "Navigating to mealDetail/$mealId")
                                navController.navigate("mealDetail/$mealId")
                            },
                            modifier = Modifier.padding(innerPadding).fillMaxSize()
                        )
                        2 -> ExerciseScreen(
                            navController = navController,
                            viewModel = exerciseViewModel,
                        )
                        3 -> StatisticsScreen(
                            modifier = Modifier.padding(innerPadding).fillMaxSize()
                        )
                        4 -> SettingsScreen(
                            modifier = Modifier.padding(innerPadding).fillMaxSize()
                        )
                    }
                }
                composable("mealDetail/{mealId}") { backStackEntry ->
                    val mealId = backStackEntry.arguments?.getString("mealId")?.toIntOrNull()
                    if (mealId == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Invalid Meal ID")
                        }
                    } else {
                        Log.d("MainScreen", "Rendering MealDetailScreen for ID: $mealId")
                        MealDetailScreen(
                            viewModel = mealsViewModel,
                            mealId = mealId,
                            apiKey = apiKey,
                            modifier = Modifier.padding(innerPadding).fillMaxSize()
                        )
                    }
                }
                composable("exerciseDetail/{exerciseId}") { backStackEntry ->
                    val exerciseId = backStackEntry.arguments?.getString("exerciseId")
                    val exercise = exerciseViewModel.exercises.value.find { it.id == exerciseId }
                    if (exercise == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Invalid Exercise ID")
                        }
                    } else {
                        Log.d("MainScreen", "Rendering ExerciseDetailScreen for ID: $exerciseId")
                        ExerciseDetailScreen(
                            exercise = exercise,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }
}