package com.example.primecare.Meals.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.primecare.Meals.MealInfoState
import com.example.primecare.Meals.MealsViewModel

@Composable
fun MealDetailScreen(
    viewModel: MealsViewModel,
    mealId: Int,
    apiKey: String,
    modifier: Modifier = Modifier
) {
    val mealInfoState = viewModel.mealInfoState.collectAsState().value

    LaunchedEffect(mealId) {
        viewModel.fetchMealInfo(mealId, apiKey)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "State: ${mealInfoState::class.simpleName}", // Debug state
            modifier = Modifier.padding(8.dp)
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (mealInfoState) {
                is MealInfoState.Idle -> {
                    Text("Preparing to load meal details...", color = Color.White)
                }
                is MealInfoState.Loading -> {
                    CircularProgressIndicator()
                }
                is MealInfoState.Success -> {
                    val mealInfo = mealInfoState.mealInfo
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                text = mealInfo.title,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        item {
                            Text(
                                text = mealInfo.summary,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        item {
                            Text(
                                text = "Ingredients",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        items(mealInfo.extendedIngredients) { ingredient ->
                            Text(
                                text = "${ingredient.name}: ${ingredient.amount} ${ingredient.unit}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                        item {
                            Text(
                                text = "Instructions",
                                style = MaterialTheme.typography.titleMedium
                                ,color = Color.White
                            )
                        }
                        mealInfo.analyzedInstructions.forEach { instruction ->
                            items(instruction.steps) { step ->
                                Text(
                                    text = "${step.number}. ${step.step}",
                                    style = MaterialTheme.typography.bodyMedium
                                    ,color = Color.White
                                )
                            }
                        }
                    }
                }
                is MealInfoState.Error -> {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Error: ${mealInfoState.message}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { viewModel.fetchMealInfo(mealId, apiKey) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}