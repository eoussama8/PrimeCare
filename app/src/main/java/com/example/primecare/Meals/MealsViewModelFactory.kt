package com.example.primecare.Meals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.primecare.Meals.api.MealApiService

class MealsViewModelFactory(private val apiService: MealApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealsViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}