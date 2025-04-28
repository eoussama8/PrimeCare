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

    fun fetchMeals(query: String, number: Int, offset: Int, apiKey: String) {
        viewModelScope.launch {
            _mealsState.value = MealsState.Loading
            try {
                Log.d("MealsViewModel", "Fetching meals with query: $query, apiKey: $apiKey")
                val meals: Meals = apiService.getMeals(query, number, offset, apiKey)
                _mealsState.value = MealsState.Success(meals)
                Log.d("MealsViewModel", "Fetched ${meals.results.size} meals")
            } catch (e: Exception) {
                _mealsState.value = MealsState.Error(e.message ?: "Failed to fetch meals")
                Log.e("MealsViewModel", "Error fetching meals: ${e.message}", e)
            }
        }
    }

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
}

sealed class MealsState {
    object Loading : MealsState()
    data class Success(val meals: Meals) : MealsState()
    data class Error(val message: String) : MealsState()
}

sealed class MealInfoState {
    object Idle : MealInfoState()
    object Loading : MealInfoState()
    data class Success(val mealInfo: Info) : MealInfoState()
    data class Error(val message: String) : MealInfoState()
}