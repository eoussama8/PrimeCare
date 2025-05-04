package com.example.primecare

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.primecare.Home.BlogScreen
import com.example.primecare.Home.BlogViewModel
import com.example.primecare.Home.CreatePostScreen
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private val themePreferences = ThemePreferences(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrimeCareTheme(themePreferences = themePreferences) {
                MainScreen(themePreferences = themePreferences)
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier,themePreferences:ThemePreferences) {
    val navController = rememberNavController()
    val mealsViewModel: MealsViewModel = viewModel(factory = MealsViewModelFactory(RetrofitClient.mealApiService))
    val exerciseViewModel: ExerciseViewModel = viewModel()
    val apiKey = BuildConfig.API_KEY
    val auth = Firebase.auth
    var selectedTab by remember { mutableStateOf(1) } // Start on Meals tab
    var currentUserId by remember { mutableStateOf(auth.currentUser?.uid ?: "") }
    var isAuthLoading by remember { mutableStateOf(auth.currentUser == null) }

    // Handle Firebase Authentication
    LaunchedEffect(Unit) {
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnSuccessListener {
                currentUserId = auth.currentUser?.uid ?: ""
                isAuthLoading = false
                Log.d("MainScreen", "Anonymous login successful: $currentUserId")
            }.addOnFailureListener { e ->
                isAuthLoading = false
                Log.e("MainScreen", "Anonymous login failed: ${e.message}")
            }
        } else {
            isAuthLoading = false
        }
    }

    // Instantiate BlogViewModel with context and currentUserId
    val blogViewModel: BlogViewModel = viewModel(
        factory = BlogViewModelFactory(LocalContext.current, currentUserId)
    )

    Log.d("MainScreen", "API Key: $apiKey") // Debug API key

    if (apiKey.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("API Key is missing. Please configure it in local.properties.")
        }
    } else if (isAuthLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
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
                        0 -> BlogScreen(
                            viewModel = blogViewModel,
                            currentUserId = currentUserId,
                            onNavigateToCreatePost = { navController.navigate("createPost") },
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

                            themePreferences = themePreferences,
                            modifier = Modifier.padding(innerPadding).fillMaxSize()
                        )
                    }
                }
                composable("createPost") {
                    CreatePostScreen(
                        viewModel = blogViewModel,
                        currentUserId = currentUserId,
                        onPostCreated = { navController.popBackStack() },
                        modifier = Modifier.padding(innerPadding).fillMaxSize()
                    )
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
                    if (exerciseId == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Invalid Exercise ID")
                        }
                    } else {
                        Log.d("MainScreen", "Rendering ExerciseDetailScreen for ID: $exerciseId")
                        val exercise by exerciseViewModel.selectedExercise.collectAsState()
                        if (exercise == null) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Exercise not found")
                            }
                        } else {
                            ExerciseDetailScreen(
                                exercise = exercise!!,
                                navController = navController,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ViewModel Factory for BlogViewModel
class BlogViewModelFactory(private val context: Context, private val currentUserId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlogViewModel(context, currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}