package com.example.primecare.Settings

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.primecare.Room.AppDB
import com.example.primecare.Room.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Male") } // Default gender
    var age by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Function to load user data from the database
    fun loadUserData() {
        scope.launch {
            try {
                Log.d("SettingsScreen", "Loading user data...")
                withContext(Dispatchers.IO) {
                    val db = AppDB.getDatabase(context)
                    val user = db.userDao().getFirstUser() // Fetch the first user
                    if (user != null) {
                        firstName = user.firstName
                        lastName = user.lastName
                        weight = user.weight.toString()
                        height = user.height.toString()
                        genre = user.genre
                        age = user.age.toString()
                        isEditMode = true // Set to edit mode if data exists
                        Log.d("SettingsScreen", "User data loaded: $user")
                    } else {
                        Log.d("SettingsScreen", "No user found in the database.")
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingsScreen", "Error loading user data: ${e.message}", e)
            }
        }
    }

    // Load user data on screen load
    LaunchedEffect(true) {
        loadUserData()
    }

    // Function to save user data to Firebase
    fun saveUserDataToFirebase(user: User) {
        try {
            val db = FirebaseFirestore.getInstance()
            // Save or update the user data in Firebase Firestore
            db.collection("users")
                .document(user.firebaseId)  // Using the firebaseId as the document ID
                .set(user)  // This will save or update the user document
                .addOnSuccessListener {
                    Log.d("SettingsScreen", "User data saved to Firebase: $user")
                }
                .addOnFailureListener { e ->
                    Log.e("SettingsScreen", "Error saving user data to Firebase: $e")
                    Toast.makeText(context, "Error saving data to Firebase", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Log.e("SettingsScreen", "Error saving user data to Firebase: ${e.message}", e)
            Toast.makeText(context, "Error saving data to Firebase", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to save or update user data
    fun saveUserData() {
        try {
            Log.d("SettingsScreen", "Attempting to save user data...")

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && weight.isNotEmpty() &&
                height.isNotEmpty() && genre.isNotEmpty() && age.isNotEmpty()) {

                // Convert weight, height, and age to Double/Int
                val weightValue = weight.toDoubleOrNull() ?: 0.0
                val heightValue = height.toDoubleOrNull() ?: 0.0
                val ageValue = age.toIntOrNull() ?: 0

                if (weightValue <= 0.0 || heightValue <= 0.0 || ageValue <= 0) {
                    Toast.makeText(context, "Please enter valid positive values.", Toast.LENGTH_SHORT).show()
                    Log.e("SettingsScreen", "Invalid input values: Weight: $weightValue, Height: $heightValue, Age: $ageValue")
                    return
                }

                val firebaseId = "userId" // Replace with actual Firebase ID

                Log.d("SettingsScreen", "Saving user data: FirstName: $firstName, LastName: $lastName, Weight: $weightValue, Height: $heightValue, Gender: $genre, Age: $ageValue")

                scope.launch {
                    val user = User(
                        firebaseId = firebaseId,
                        firstName = firstName,
                        lastName = lastName,
                        weight = weightValue,
                        height = heightValue,
                        genre = genre,
                        age = ageValue
                    )

                    // Save to Firebase Firestore
                    saveUserDataToFirebase(user)

                    // Save to Room database
                    withContext(Dispatchers.IO) {
                        val db = AppDB.getDatabase(context)
                        try {
                            if (isEditMode) {
                                db.userDao().update(user) // Update if data already exists
                                Log.d("SettingsScreen", "User data updated in Room: $user")
                            } else {
                                db.userDao().insert(user) // Insert new user if no data
                                Log.d("SettingsScreen", "New user data saved in Room: $user")
                            }
                        } catch (e: Exception) {
                            Log.e("SettingsScreen", "Error saving user data to Room: ${e.message}", e)
                        }
                    }

                    // Show a Toast message to confirm saving
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, if (isEditMode) "User data updated" else "User data saved", Toast.LENGTH_SHORT).show()
                        isEditMode = true // Set to edit mode after saving
                    }
                }
            } else {
                Log.e("SettingsScreen", "Please fill in all fields.")
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("SettingsScreen", "Error saving user data: ${e.message}", e)
            Toast.makeText(context, "Error saving user data", Toast.LENGTH_SHORT).show()
        }
    }

    // UI to manipulate user data
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isEditMode) "Edit User Data" else "Settings Screen",
            color = Color.White,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // TextField for First Name
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // TextField for Last Name
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // TextField for Weight
        TextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // TextField for Height
        TextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Gender Selection (Radio Buttons)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "Gender: ", color = Color.White, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(
                selected = genre == "Male",
                onClick = { genre = "Male" }
            )
            Text(text = "Male", color = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = genre == "Female",
                onClick = { genre = "Female" }
            )
            Text(text = "Female", color = Color.White)
        }

        // TextField for Age
        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* Handle Done action if needed */ }
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Save/Update Button
        Button(onClick = { saveUserData() }) {
            Text(text = if (isEditMode) "Update" else "Save", fontSize = 18.sp)
        }
    }
}
