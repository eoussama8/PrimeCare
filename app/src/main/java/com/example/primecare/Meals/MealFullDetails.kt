package com.example.primecare.Meals.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.primecare.Meals.MealInfoState
import com.example.primecare.Meals.MealsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun MealDetailScreen(
    viewModel: MealsViewModel,
    mealId: Int,
    apiKey: String,
    modifier: Modifier = Modifier
) {
    val mealInfoState = viewModel.mealInfoState.collectAsState().value
    var isSaved by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(mealId) {
        viewModel.fetchMealInfo(mealId, apiKey)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Meal Details",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Implement back navigation */ }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSaved = !isSaved }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isSaved) "Remove from favorites" else "Add to favorites",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when (mealInfoState) {
            is MealInfoState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Preparing to load meal details...",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is MealInfoState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading meal details...",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            is MealInfoState.Success -> {
                val mealInfo = mealInfoState.mealInfo
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    item {
                        // Hero section with meal image and basic info
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Meal image with border
                                Box(
                                    modifier = Modifier
                                        .padding(top = 16.dp, bottom = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    mealInfo.image?.let { url ->
                                        Box(
                                            modifier = Modifier
                                                .size(200.dp, 280.dp)
                                                .border(
                                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(1.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        ) {
                                            GlideImage(
                                                model = url,
                                                contentDescription = "Meal Image",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            ) {
                                                it.placeholder(android.R.drawable.ic_menu_gallery)
                                                    .error(android.R.drawable.ic_menu_report_image)
                                            }
                                        }
                                    } ?: Box(
                                        modifier = Modifier
                                            .size(200.dp, 280.dp)
                                            .border(
                                                BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .background(MaterialTheme.colorScheme.outline),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = android.R.drawable.ic_menu_report_image),
                                            contentDescription = "No Image",
                                            modifier = Modifier.size(60.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                // Title
                                Text(
                                    text = mealInfo.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                // Optional metadata (e.g., cuisine or diet)
                                mealInfo.cuisines?.let { cuisines ->
                                    if (cuisines.isNotEmpty()) {
                                        Text(
                                            text = cuisines.joinToString(", "),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                                        )
                                    }
                                }

                                // Diet as pills
                                mealInfo.diets?.let { diets ->
                                    if (diets.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier
                                                .padding(vertical = 8.dp)
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            diets.take(2).forEach { diet ->
                                                CategoryPill(category = diet)
                                                Spacer(modifier = Modifier.width(8.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                    }

                    // Summary section with expand/collapse
                    item {
                        mealInfo.summary?.let { summary ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "About this meal",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = if (expanded) Int.MAX_VALUE else 4,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.animateContentSize()
                                )

                                TextButton(
                                    onClick = { expanded = !expanded },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(
                                        text = if (expanded) "Show less" else "Read more",
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                        }
                    }

                    // Ingredients section
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Ingredients",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    items(mealInfo.extendedIngredients) { ingredient ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ingredient.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(0.6f)
                            )
                            Text(
                                text = "${ingredient.amount} ${ingredient.unit}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(0.4f),
                                textAlign = TextAlign.End
                            )
                        }
                    }

                    item {
                        Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                    }

                    // Instructions section
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Instructions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    mealInfo.analyzedInstructions.forEach { instruction ->
                        items(instruction.steps) { step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${step.number}.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.width(32.dp)
                                )
                                Text(
                                    text = step.step,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
            is MealInfoState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_report_image),
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(60.dp)
                        )
                        Text(
                            text = "Error: ${mealInfoState.message}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.fetchMealInfo(mealId, apiKey) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Retry", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(category: String) {
    Box(
        modifier = Modifier
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}