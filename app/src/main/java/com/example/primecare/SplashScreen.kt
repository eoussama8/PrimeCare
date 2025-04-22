package com.example.primecare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.primecare.ui.theme.PrimeCareTheme
import com.example.primecare.ui.theme.Typography
import com.example.primecare.OnBoarding.OnBoardingActivity
import com.example.primecare.data.ThemePreferences
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themePreferences = ThemePreferences(this)

        setContent {
            PrimeCareTheme(themePreferences = themePreferences) {
                SplashScreen()
            }
        }
    }
}

@Composable
fun SplashScreen() {
    val context = LocalContext.current
    val logoScale = remember { Animatable(0.7f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val descriptionAlpha = remember { Animatable(0f) }

    // Animations sequence
    LaunchedEffect(Unit) {
        logoAlpha.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        logoScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.6f, stiffness = 100f))
        textAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        descriptionAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))

        delay(1800)
        val intent = Intent(context, OnBoardingActivity::class.java)
        context.startActivity(intent)
        (context as? ComponentActivity)?.finish()
    }

    // Use MaterialTheme's background color for light/dark mode
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Logo with animation
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        alpha = logoAlpha.value
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name with MaterialTheme's onBackground color for contrast
            Text(
                text = stringResource(id = R.string.app_name),
                color = MaterialTheme.colorScheme.primary, // Adapts to light/dark mode
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                letterSpacing = 1.2.sp,
                style = Typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                ),
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha.value
                }
            )

        }
    }
}

