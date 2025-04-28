package com.example.primecare.Meals.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.primecare.Meals.MealsViewModel
import com.example.primecare.Meals.MealsState
import com.example.primecare.Meals.api.Result

@Composable
fun MealsScreen(
    viewModel: MealsViewModel,
    apiKey: String,
    onMealClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val mealsState = viewModel.mealsState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.fetchMeals(query = "", number = 10, offset = 0, apiKey = apiKey)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "State: ${mealsState::class.simpleName}", // Debug state
            modifier = Modifier.padding(8.dp)
        )
        when (mealsState) {
            is MealsState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MealsState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(mealsState.meals.results) { meal ->
                        MealItem(meal = meal, onClick = { onMealClick(meal.id) })
                    }
                }
            }
            is MealsState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Error: ${mealsState.message}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(onClick = {
                            viewModel.fetchMeals(query = "", number = 10, offset = 0, apiKey = apiKey)
                        }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealItem(meal: Result, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                Log.d("MealsScreen", "Clicked meal ID: ${meal.id}")
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = meal.image,
                contentDescription = meal.title,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = meal.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}