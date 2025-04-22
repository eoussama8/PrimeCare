package com.example.primecare.OnBoarding


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.primecare.MainActivity
import com.example.primecare.data.ThemePreferences
import com.example.primecare.ui.theme.PrimeCareTheme

class OnBoardingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("isFirstRun", true)
        val themePreferences = ThemePreferences(this) // ← add this line

        if (!isFirstRun) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContent {
            PrimeCareTheme(themePreferences = themePreferences) { // ← pass it here
                Surface(modifier = Modifier.fillMaxSize()) {
                    OnBoarding(
                        onFinish = {
                            prefs.edit().putBoolean("isFirstRun", false).apply()
                            startActivity(Intent(this@OnBoardingActivity, MainActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }

    // Helper function to navigate to MainActivity
    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()  // Close the OnBoardingActivity
    }
}