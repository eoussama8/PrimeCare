package com.example.primecare.Settings

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.primecare.Room.AppDB
import com.example.primecare.Room.User
import com.example.primecare.data.ThemeMode
import com.example.primecare.data.ThemePreferences
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SettingsScreen(
    themePreferences: ThemePreferences,
    modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Male") }
    var age by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val themeMode = themePreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM).value
    val focusManager = LocalFocusManager.current

    // Track expanded sections
    var isProfileExpanded by remember { mutableStateOf(false) }
    var isThemeExpanded by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var isAboutExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Function to load user data from the database
    fun loadUserData() {
        scope.launch {
            try {
                Log.d("SettingsScreen", "Loading user data...")
                withContext(Dispatchers.IO) {
                    val db = AppDB.getDatabase(context)
                    val user = db.userDao().getFirstUser()
                    if (user != null) {
                        firstName = user.firstName
                        lastName = user.lastName
                        weight = user.weight.toString()
                        height = user.height.toString()
                        genre = user.genre
                        age = user.age.toString()
                        isEditMode = true
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
            db.collection("users")
                .document(user.firebaseId)
                .set(user)
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
                                db.userDao().update(user)
                                Log.d("SettingsScreen", "User data updated in Room: $user")
                            } else {
                                db.userDao().insert(user)
                                Log.d("SettingsScreen", "New user data saved in Room: $user")
                            }
                        } catch (e: Exception) {
                            Log.e("SettingsScreen", "Error saving user data to Room: ${e.message}", e)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, if (isEditMode) "User data updated" else "User data saved", Toast.LENGTH_SHORT).show()
                        isEditMode = true
                        focusManager.clearFocus()
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

    // UI with collapsible sections
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.Start)
            )

            // Profile Form Section
            SettingsSection(
                title = "Profile Settings",
                icon = Icons.Default.Person,
                isExpanded = isProfileExpanded,
                onToggle = { isProfileExpanded = !isProfileExpanded }
            ) {
                ProfileContent(
                    firstName = firstName,
                    lastName = lastName,
                    weight = weight,
                    height = height,
                    genre = genre,
                    age = age,
                    isEditMode = isEditMode,
                    onFirstNameChange = { firstName = it },
                    onLastNameChange = { lastName = it },
                    onWeightChange = { weight = it },
                    onHeightChange = { height = it },
                    onGenreChange = { genre = it },
                    onAgeChange = { age = it },
                    onSave = { saveUserData() },
                    focusManager = focusManager
                )
            }

            // Theme Settings Section
            SettingsSection(
                title = "Theme Settings",
                icon = Icons.Outlined.DarkMode,
                isExpanded = isThemeExpanded,
                onToggle = { isThemeExpanded = !isThemeExpanded }
            ) {
                ThemeContent(
                    themeMode = themeMode,
                    onThemeModeChange = { newMode ->
                        scope.launch {
                            themePreferences.setThemeMode(newMode)
                        }
                    }
                )
            }

            // Search App Section
            SettingsSection(
                title = "Search",
                icon = Icons.Default.Search,
                isExpanded = isSearchExpanded,
                onToggle = { isSearchExpanded = !isSearchExpanded }
            ) {
                SearchContent(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onSearch = {
                        Toast.makeText(context, "Searching for: $searchQuery", Toast.LENGTH_SHORT).show()
                        focusManager.clearFocus()
                    }
                )
            }

            // About Section
            SettingsSection(
                title = "About",
                icon = Icons.Outlined.Info,
                isExpanded = isAboutExpanded,
                onToggle = { isAboutExpanded = !isAboutExpanded }
            ) {
                AboutContent()
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
            ) {
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    firstName: String,
    lastName: String,
    weight: String,
    height: String,
    genre: String,
    age: String,
    isEditMode: Boolean,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onGenreChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onSave: () -> Unit,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isEditMode) "Edit Profile" else "Create Profile",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.Start)
        )

        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = { Text("First Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = { Text("Last Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                label = { Text("Weight (kg)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            OutlinedTextField(
                value = height,
                onValueChange = onHeightChange,
                label = { Text("Height (cm)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }

        OutlinedTextField(
            value = age,
            onValueChange = onAgeChange,
            label = { Text("Age") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Text(
            text = "Gender",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = genre == "Male",
                onClick = { onGenreChange("Male") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "Male",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.width(24.dp))

            RadioButton(
                selected = genre == "Female",
                onClick = { onGenreChange("Female") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "Female",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (isEditMode) "Update Profile" else "Save Profile",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ThemeContent(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Choose Theme",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThemeOption(
                title = "Light",
                selected = themeMode == ThemeMode.LIGHT,
                onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                modifier = Modifier.weight(1f)
            )

            ThemeOption(
                title = "Dark",
                selected = themeMode == ThemeMode.DARK,
                onClick = { onThemeModeChange(ThemeMode.DARK) },
                modifier = Modifier.weight(1f)
            )

            ThemeOption(
                title = "System",
                selected = themeMode == ThemeMode.SYSTEM,
                onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ThemeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SearchContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search in App") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Button(
            onClick = onSearch,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Search",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun AboutContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "PrimeCare App",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                InfoRow(label = "Version", value = "1.0.0")
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(label = "Developed by", value = "PrimeCare Team")
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(label = "Contact", value = "support@primecare.com")
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(label = "Copyright", value = "© 2025 PrimeCare Inc.")
            }
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}