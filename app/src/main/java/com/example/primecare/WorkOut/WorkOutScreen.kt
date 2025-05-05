package com.example.primecare.WorkOut

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.primecare.WorkOut.api.Exercise
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@Composable
fun ExerciseScreen(navController: NavController, viewModel: ExerciseViewModel = viewModel()) {
    val exercises by viewModel.exercises.collectAsState()
    val bodyParts by viewModel.bodyParts.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedBodyPart by remember { mutableStateOf<String?>(null) }
    var showSearch by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(false) } // Default to list view
    val swipeRefreshState = rememberSwipeRefreshState(isLoading)

    LaunchedEffect(Unit) {
        viewModel.fetchExercises()
    }

    Scaffold(
        topBar = {
            ExerciseTopAppBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { query ->
                    searchQuery = query
                    viewModel.searchExercises(query)
                },
                showSearch = showSearch,
                onSearchToggle = { showSearch = it },
                isGridView = isGridView,
                onViewToggle = { isGridView = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Body Part Filter
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    CategoryText(
                        category = "All",
                        isSelected = selectedBodyPart == null,
                        onClick = {
                            selectedBodyPart = null
                            viewModel.fetchExercises()
                        }
                    )
                }
                items(bodyParts) { bodyPart ->
                    CategoryText(
                        category = bodyPart.replaceFirstChar { it.uppercase() },
                        isSelected = selectedBodyPart == bodyPart,
                        onClick = {
                            selectedBodyPart = bodyPart
                            viewModel.fetchExercisesByBodyPart(bodyPart)
                        }
                    )
                }
            }

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { viewModel.fetchExercises() },
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        error != null -> {
                            ErrorMessage(error!!) {
                                viewModel.fetchExercises()
                            }
                        }
                        isLoading -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(6) {
                                    PlaceholderExerciseItem()
                                }
                            }
                        }
                        exercises.isEmpty() -> {
                            EmptyStateMessage()
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(exercises, key = { it.id }) { exercise ->
                                    ExerciseItem(
                                        exercise = exercise,
                                        onClick = {
                                            viewModel.selectExercise(exercise.id)
                                            navController.navigate("exerciseDetail/${exercise.id}")
                                        },
                                        onToggleSave = { /* TODO: Implement save logic if needed */ }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    showSearch: Boolean,
    onSearchToggle: (Boolean) -> Unit,
    isGridView: Boolean,
    onViewToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (showSearch) 100.dp else 60.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PrimeCare Exercises",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onSearchToggle(!showSearch) }, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = { onViewToggle(!isGridView) }, modifier = Modifier.size(40.dp)) {
                        Icon(
                            painter = painterResource(id = if (isGridView) android.R.drawable.ic_menu_view else android.R.drawable.ic_menu_sort_by_size),
                            contentDescription = "Toggle View",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = showSearch,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            "Search by exercise name",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                )
            }
        }
    }
}

@Composable
fun CategoryText(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = category,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ExerciseItem(
    exercise: Exercise,
    onClick: () -> Unit,
    onToggleSave: () -> Unit
) {
    var isSaved by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            ) {
                if (!exercise.gifUrl.isNullOrEmpty()) {
                    GlideImage(
                        model = exercise.gifUrl,
                        contentDescription = "${exercise.name} GIF",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    ) {
                        it.placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_report_image)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.outline),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_report_image),
                            contentDescription = "No Image",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                                ),
                                startY = 80f
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = exercise.name,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Body Part: ${exercise.bodyPart.replaceFirstChar { it.uppercase() }}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = "Details",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(
                        onClick = { isSaved = !isSaved; onToggleSave() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = if (isSaved) android.R.drawable.star_on else android.R.drawable.star_off),
                            contentDescription = if (isSaved) "Unsave" else "Save",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
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
                contentDescription = "No Exercises Found",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Text(
                text = "No exercises found",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_report_image),
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(60.dp)
            )
            Text(
                text = "Error: $message",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retry", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun PlaceholderExerciseItem() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .shimmer()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(16.dp)
                            .shimmer()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(12.dp)
                            .shimmer()
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.shimmer(): Modifier {
    var offset by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            offset += 0.02f
            if (offset > 1.2f) offset = -0.2f
            delay(16)
        }
    }
    return this.background(
        brush = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            ),
            start = Offset(offset * 1000f, 0f),
            end = Offset((offset + 0.4f) * 1000f, 0f)
        )
    )
}