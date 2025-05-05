package com.example.primecare.WorkOut

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
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.primecare.WorkOut.api.Exercise

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun ExerciseDetailScreen(exercise: Exercise, navController: NavController) {
    var isSaved by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Exercise Details",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Hero section with exercise image and basic info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Exercise image with border
                        Box(
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            exercise.gifUrl?.let { url ->
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
                                        contentDescription = "${exercise.name} GIF",
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
                            text = exercise.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Metadata (body part, target muscle)
                        Text(
                            text = "${exercise.bodyPart.replaceFirstChar { it.uppercase() }} • ${exercise.target.replaceFirstChar { it.uppercase() }}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )

                        // Equipment as a pill
                        Row(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CategoryPill(category = exercise.equipment.replaceFirstChar { it.uppercase() })
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
            }

            // Details section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Exercise Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Body Part
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Body Part",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.4f)
                        )
                        Text(
                            text = exercise.bodyPart.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.6f)
                        )
                    }

                    // Target Muscle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target Muscle",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.4f)
                        )
                        Text(
                            text = exercise.target.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.6f)
                        )
                    }

                    // Equipment
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Equipment",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.4f)
                        )
                        Text(
                            text = exercise.equipment.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.6f)
                        )
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
            }

            // Secondary Muscles section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Secondary Muscles",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            items(exercise.secondaryMuscles) { muscle ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.width(32.dp)
                    )
                    Text(
                        text = muscle.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
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

                    Text(
                        text = exercise.instructions.joinToString("\n") { "${exercise.instructions.indexOf(it) + 1}. $it" },
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