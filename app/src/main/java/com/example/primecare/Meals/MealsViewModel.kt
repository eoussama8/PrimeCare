package com.example.primecare.Meals

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.primecare.Meals.api.MealApiService
import com.example.primecare.Meals.api.Meals
import com.example.primecare.Meals.api.Info
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealsViewModel(private val apiService: MealApiService) : ViewModel() {

    private val _mealsState = MutableStateFlow<MealsState>(MealsState.Loading)
    val mealsState: StateFlow<MealsState> = _mealsState

    private val _mealInfoState = MutableStateFlow<MealInfoState>(MealInfoState.Idle)
    val mealInfoState: StateFlow<MealInfoState> = _mealInfoState

    private val _categoriesState = MutableStateFlow<CategoriesState>(CategoriesState.Idle)
    val categoriesState: StateFlow<CategoriesState> = _categoriesState

    // Fetch meals with search query and optional category filters
    fun fetchMeals(
        query: String = "",
        number: Int = 10,
        offset: Int = 0,
        cuisine: String? = null,
        type: String? = null,
        diet: String? = null,
        apiKey: String
    ) {
        viewModelScope.launch {
            _mealsState.value = MealsState.Loading
            try {
                Log.d("MealsViewModel", "Fetching meals with query: $query, cuisine: $cuisine, type: $type, diet: $diet, apiKey: $apiKey")
                val meals: Meals = apiService.getMeals(query, number, offset, cuisine, type, diet, apiKey)
                _mealsState.value = MealsState.Success(meals)
                Log.d("MealsViewModel", "Fetched ${meals.results.size} meals")
            } catch (e: Exception) {
                _mealsState.value = MealsState.Error(e.message ?: "Failed to fetch meals")
                Log.e("MealsViewModel", "Error fetching meals: ${e.message}", e)
            }
        }
    }

    // Fetch meal details by ID
    fun fetchMealInfo(id: Int, apiKey: String) {
        viewModelScope.launch {
            _mealInfoState.value = MealInfoState.Loading
            try {
                Log.d("MealsViewModel", "Fetching meal info for ID: $id with apiKey: $apiKey")
                val mealInfo: Info = apiService.getMealInfo(id, apiKey)
                _mealInfoState.value = MealInfoState.Success(mealInfo)
                Log.d("MealsViewModel", "Fetched meal: ${mealInfo.title}")
            } catch (e: Exception) {
                _mealInfoState.value = MealInfoState.Error(e.message ?: "Failed to fetch meal info")
                Log.e("MealsViewModel", "Error fetching meal info for ID $id: ${e.message}", e)
            }
        }
    }

    // Fetch or provide available categories (static for simplicity)
    fun fetchCategories() {
        viewModelScope.launch {
            _categoriesState.value = CategoriesState.Loading
            try {
                // Static categories (could be fetched from an API if available)
                val categories = Categories(
                    cuisines = listOf("Italian", "Mexican", "Indian", "Chinese", "American"),
                    types = listOf("Main Course", "Dessert", "Appetizer", "Breakfast", "Soup"),
                    diets = listOf("Vegan", "Vegetarian", "Gluten-Free", "Keto", "Paleo")
                )
                _categoriesState.value = CategoriesState.Success(categories)
                Log.d("MealsViewModel", "Loaded categories")
            } catch (e: Exception) {
                _categoriesState.value = CategoriesState.Error(e.message ?: "Failed to fetch categories")
                Log.e("MealsViewModel", "Error fetching categories: ${e.message}", e)
            }
        }
    }

    // Optionally, you could expose methods to get specific categories if needed
    fun getCategories(): CategoriesState {
        return _categoriesState.value
    }}

// State for meals
sealed class MealsState {
    object Loading : MealsState()
    data class Success(val meals: Meals) : MealsState()
    data class Error(val message: String) : MealsState()
}

// State for meal info
sealed class MealInfoState {
    object Idle : MealInfoState()
    object Loading : MealInfoState()
    data class Success(val mealInfo: Info) : MealInfoState()
    data class Error(val message: String) : MealInfoState()
}

// State for categories
sealed class CategoriesState {
    object Idle : CategoriesState()
    object Loading : CategoriesState()
    data class Success(val categories: Categories) : CategoriesState()
    data class Error(val message: String) : CategoriesState()
}

// Data class for categories
data class Categories(
    val cuisines: List<String>,
    val types: List<String>,
    val diets: List<String>
)