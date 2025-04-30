package com.example.primecare.Meals.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.primecare.Meals.MealsViewModel
import com.example.primecare.Meals.MealsState
import com.example.primecare.Meals.CategoriesState
import com.example.primecare.Meals.Categories
import com.example.primecare.Meals.api.Result

@Composable
fun MealsScreen(
    viewModel: MealsViewModel,
    apiKey: String,
    onMealClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val mealsState = viewModel.mealsState.collectAsState().value
    val categoriesState = viewModel.categoriesState.collectAsState().value
    var searchQuery by remember { mutableStateOf("") }
    var selectedCuisine by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var selectedDiet by remember { mutableStateOf<String?>(null) }

    // Fetch initial data
    LaunchedEffect(Unit) {
        viewModel.fetchMeals(
            query = searchQuery,
            number = 10,
            offset = 0,
            cuisine = selectedCuisine,
            type = selectedType,
            diet = selectedDiet,
            apiKey = apiKey
        )
        viewModel.fetchCategories()
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { newQuery ->
                searchQuery = newQuery
                viewModel.fetchMeals(
                    query = newQuery,
                    number = 10,
                    offset = 0,
                    cuisine = selectedCuisine,
                    type = selectedType,
                    diet = selectedDiet,
                    apiKey = apiKey
                )
            },
            label = { Text("Search meals") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )

        // Category Filters
        when (categoriesState) {
            is CategoriesState.Success -> {
                CategoryFilters(
                    categories = categoriesState.categories,
                    selectedCuisine = selectedCuisine,
                    onCuisineSelected = { cuisine ->
                        selectedCuisine = cuisine
                        viewModel.fetchMeals(
                            query = searchQuery,
                            number = 10,
                            offset = 0,
                            cuisine = cuisine,
                            type = selectedType,
                            diet = selectedDiet,
                            apiKey = apiKey
                        )
                    },
                    selectedType = selectedType,
                    onTypeSelected = { type ->
                        selectedType = type
                        viewModel.fetchMeals(
                            query = searchQuery,
                            number = 10,
                            offset = 0,
                            cuisine = selectedCuisine,
                            type = type,
                            diet = selectedDiet,
                            apiKey = apiKey
                        )
                    },
                    selectedDiet = selectedDiet,
                    onDietSelected = { diet ->
                        selectedDiet = diet
                        viewModel.fetchMeals(
                            query = searchQuery,
                            number = 10,
                            offset = 0,
                            cuisine = selectedCuisine,
                            type = selectedType,
                            diet = diet,
                            apiKey = apiKey
                        )
                    }
                )
            }
            is CategoriesState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            is CategoriesState.Error -> {
                Text(
                    text = "Error loading categories: ${categoriesState.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            is CategoriesState.Idle -> {
                // Do nothing until categories are fetched
            }
        }

        // Meals List
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
                            viewModel.fetchMeals(
                                query = searchQuery,
                                number = 10,
                                offset = 0,
                                cuisine = selectedCuisine,
                                type = selectedType,
                                diet = selectedDiet,
                                apiKey = apiKey
                            )
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
fun CategoryFilters(
    categories: Categories,
    selectedCuisine: String?,
    onCuisineSelected: (String?) -> Unit,
    selectedType: String?,
    onTypeSelected: (String?) -> Unit,
    selectedDiet: String?,
    onDietSelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Cuisine Dropdown
        CategoryDropdown(
            label = "Cuisine",
            items = categories.cuisines,
            selectedItem = selectedCuisine,
            onItemSelected = onCuisineSelected
        )

        // Dish Type Dropdown
        CategoryDropdown(
            label = "Dish Type",
            items = categories.types,
            selectedItem = selectedType,
            onItemSelected = onTypeSelected
        )

        // Diet Dropdown
        CategoryDropdown(
            label = "Diet",
            items = categories.diets,
            selectedItem = selectedDiet,
            onItemSelected = onDietSelected
        )
    }
}


@Composable
fun CategoryDropdown(
    label: String,
    items: List<String>,
    selectedItem: String?,
    onItemSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedItem ?: "Select $label",
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = if (expanded) {
                            androidx.compose.material.icons.Icons.Default.ArrowDropUp
                        } else {
                            androidx.compose.material.icons.Icons.Default.ArrowDropDown
                        },
                        contentDescription = null
                    )
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            // Option to clear selection
            DropdownMenuItem(
                text = { Text("All") },
                onClick = {
                    onItemSelected(null)
                    expanded = false
                }
            )
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
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