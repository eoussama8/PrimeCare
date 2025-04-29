package com.example.primecare.WorkOut

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.primecare.WorkOut.api.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(exercise: Exercise, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AsyncImage(
                    model = exercise.gifUrl,
                    contentDescription = "${exercise.name} GIF",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
            item {
                Text(
                    text = "Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Body Part: ${exercise.bodyPart.replaceFirstChar { it.uppercase() }}")
                Text("Target Muscle: ${exercise.target.replaceFirstChar { it.uppercase() }}")
                Text("Equipment: ${exercise.equipment.replaceFirstChar { it.uppercase() }}")
            }
            item {
                Text(
                    text = "Secondary Muscles",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                exercise.secondaryMuscles.forEach { muscle ->
                    Text("• ${muscle.replaceFirstChar { it.uppercase() }}")
                }
            }
            item {
                Text(
                    text = "Instructions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                exercise.instructions.forEachIndexed { index, instruction ->
                    Text("${index + 1}. $instruction")
                }
            }
        }
    }
}