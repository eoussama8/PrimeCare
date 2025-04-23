package com.example.primecare.Auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.primecare.data.ThemePreferences
import com.example.primecare.ui.theme.PrimeCareTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private val themePreferences by lazy { ThemePreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            PrimeCareTheme(themePreferences = themePreferences) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RegisterScreen { email, password ->
                        registerUser(email, password)
                    }
                }
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Account created successfully: ${auth.currentUser?.email}",
                        Toast.LENGTH_LONG
                    ).show()
                    // Navigate to LoginActivity after successful registration
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish() // Close RegisterActivity
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}