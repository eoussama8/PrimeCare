package com.example.primecare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.primecare.ui.theme.PrimeCareTheme
import com.example.primecare.ui.theme.Typography
import com.example.primecare.OnBoarding.OnBoardingActivity
import com.example.primecare.data.ThemePreferences
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class QuoteResponse(
    val _id: String,
    val content: String,
    val author: String,
    val tags: List<String>,
    val authorSlug: String,
    val length: Int,
    val dateAdded: String,
    val dateModified: String
)

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
    var showQuote by remember { mutableStateOf(false) }

    var quote by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        try {
            val response = client.get("http://api.quotable.io/random").body<QuoteResponse>()
            quote = response.content
            author = response.author
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error fetching quote: ${e.message}", e)
            quote = "Stay positive and keep moving forward!"
            author = "PrimeCare"
        } finally {
            client.close()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        // Sequential animations for a smoother experience
        logoAlpha.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        logoScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.6f, stiffness = 100f))
        textAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        delay(500)
        showQuote = true
        delay(3000)

        // Navigate to next screen
        val intent = Intent(context, OnBoardingActivity::class.java)
        context.startActivity(intent)
        (context as? ComponentActivity)?.finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Direct logo without container
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        alpha = logoAlpha.value
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                letterSpacing = 1.sp,
                style = Typography.headlineLarge,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha.value
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your Health, Our Priority",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                style = Typography.bodyMedium,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha.value
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Simplified quote section
            AnimatedVisibility(
                visible = showQuote,
                enter = fadeIn(tween(500)) + slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = tween(500)
                ),
                exit = fadeOut()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(28.dp)
                            .padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    // Simplified card with quote
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = quote,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Italic,
                                style = Typography.bodyLarge,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "— $author",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp,
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Medium,
                                style = Typography.labelMedium,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}