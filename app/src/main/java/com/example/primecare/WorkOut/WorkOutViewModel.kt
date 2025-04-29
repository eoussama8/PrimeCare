package com.example.primecare.WorkOut

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.primecare.WorkOut.api.Exercise
import com.example.primecare.WorkOut.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class ExerciseViewModel : ViewModel() {
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    private val _selectedExercise = MutableStateFlow<Exercise?>(null)
    val selectedExercise: StateFlow<Exercise?> = _selectedExercise

    private val _bodyParts = MutableStateFlow<List<String>>(emptyList())
    val bodyParts: StateFlow<List<String>> = _bodyParts

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val apiKey = "054f89be43msh9aacee51318bb43p1e21f3jsnb83190ff87b2" // Replace with your RapidAPI key
    private val apiHost = "exercisedb.p.rapidapi.com"

    init {
        fetchBodyParts()
        fetchExercises()
    }

    fun selectExercise(exerciseId: String) {
        viewModelScope.launch {
            val exercise = _exercises.value.find { it.id == exerciseId }
            _selectedExercise.value = exercise
        }
    }

    fun fetchExercises() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: Response<List<Exercise>> = RetrofitClient.exerciseApiService.getExercises(
                    apiKey = apiKey,
                    apiHost = apiHost
                )
                handleResponse(response)
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchExercises(query: String) {
        if (query.isBlank()) {
            fetchExercises()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: Response<List<Exercise>> = RetrofitClient.exerciseApiService.searchExercises(
                    name = query.trim(),
                    apiKey = apiKey,
                    apiHost = apiHost
                )
                handleResponse(response)
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchExercisesByBodyPart(bodyPart: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: Response<List<Exercise>> = RetrofitClient.exerciseApiService.getExercisesByBodyPart(
                    bodyPart = bodyPart.lowercase(),
                    apiKey = apiKey,
                    apiHost = apiHost
                )
                handleResponse(response)
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchBodyParts() {
        viewModelScope.launch {
            try {
                val response: Response<List<String>> = RetrofitClient.exerciseApiService.getBodyParts(
                    apiKey = apiKey,
                    apiHost = apiHost
                )
                if (response.isSuccessful) {
                    _bodyParts.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error fetching body parts: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            }
        }
    }

    private fun handleResponse(response: Response<List<Exercise>>) {
        if (response.isSuccessful) {
            _exercises.value = response.body() ?: emptyList()
            _error.value = null
        } else {
            _error.value = "Error: ${response.message()}"
        }
    }
}