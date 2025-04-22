package com.example.primecare.OnBoarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.primecare.ui.theme.Typography
import androidx.compose.ui.zIndex
import com.example.primecare.ui.theme.Card
import com.example.primecare.ui.theme.MainColor
import com.example.primecare.ui.theme.White
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

// Theme colors for clarity
private val PrimaryColor = MainColor
private val SecondaryColor = Card
private val BackgroundColor = White

@Composable
fun OnBoarding(onFinish: () -> Unit = {}) {
    val items = OnBoardingItems.getData()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { items.size })
    val currentPage = pagerState.currentPage

    Box(modifier = Modifier.fillMaxSize()) {
        // Background with gradient overlay
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val absPageOffset = pageOffset.absoluteValue

            // Simple parallax and fade effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 1f - (absPageOffset * 0.5f).coerceAtMost(1f)
                        translationX = size.width * (pageOffset * -0.1f)
                    }
            ) {
                // Background Image
                Image(
                    painter = painterResource(id = items[page].image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay with gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                )

                // Content
                OnBoardingContent(
                    items = items[page],
                    isLastPage = page == items.size - 1,
                    onButtonClick = {
                        if (page == items.size - 1) {
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    page + 1,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }

        // Top bar with progress and skip button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .zIndex(2f)
        ) {
            // Simple progress indicator
            val animatedProgress by animateFloatAsState(
                targetValue = (currentPage + 1) / items.size.toFloat(),
                animationSpec = tween(300),
                label = "progress"
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(2.dp))
                    .align(Alignment.CenterStart)
                    .semantics { contentDescription = "Progress ${currentPage + 1} of ${items.size}" },
                color = PrimaryColor,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            // Skip button
            TextButton(
                onClick = { onFinish() },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .semantics { contentDescription = "Skip onboarding" }
            ) {
                Text(
                    text = "Skip",
                    style = Typography.labelSmall.copy(
                        color = Color.White,
                        fontSize = 16.sp
                    )
                )
            }
        }

        // Bottom page indicators
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        ) {
            repeat(items.size) { position ->
                PageIndicator(
                    isSelected = position == currentPage,
                    position = position,
                    totalDots = items.size
                )
            }
        }
    }
}

@Composable
fun PageIndicator(
    isSelected: Boolean,
    position: Int,
    totalDots: Int
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .width(if (isSelected) 24.dp else 8.dp)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (isSelected) PrimaryColor else Color.White.copy(alpha = 0.5f)
            )
    )
}

@Composable
fun OnBoardingContent(
    items: OnBoardingItems,
    isLastPage: Boolean,
    onButtonClick: () -> Unit
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                // Icon
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300)) + slideInVertically(
                        initialOffsetY = { with(density) { 50.dp.roundToPx() } },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(SecondaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        items.icon?.let { iconResId ->
                            Image(
                                painter = painterResource(id = iconResId),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                            )
                        } ?: Icon(
                            painter = painterResource(id = android.R.drawable.ic_dialog_info),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300, delayMillis = 150)) + slideInVertically(
                        initialOffsetY = { with(density) { 30.dp.roundToPx() } },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Text(
                        text = stringResource(id = items.title),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = PrimaryColor,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Divider
                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .width(40.dp)
                        .height(3.dp)
                        .background(
                            PrimaryColor.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(1.5.dp)
                        )
                )

                // Description
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300, delayMillis = 250)) + slideInVertically(
                        initialOffsetY = { with(density) { 20.dp.roundToPx() } },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    Text(
                        text = stringResource(id = items.description),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Button
                Button(
                    onClick = onButtonClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .height(54.dp)
                        .semantics {
                            contentDescription = if (isLastPage) "Get Started" else "Continue to next page"
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isLastPage) "Get Started" else "Continue",
                            style = Typography.labelSmall.copy(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        if (!isLastPage) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_media_play),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun OnBoardingContent(
    items: OnBoardingItems,
    isLastPage: Boolean,
    currentPage: Int,
    onButtonClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Enhanced content card with improved animations and effects
        Card(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    shadowElevation = 12f
                },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = BackgroundColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                // 1. Icon with enhanced design and animations
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(600)) +
                            scaleIn(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                initialScale = 0.2f
                            )
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        SecondaryColor.copy(alpha = 0.6f),
                                        SecondaryColor
                                    )
                                )
                            )
                            .shadow(8.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Multiple animated effects for the icon
                        val iconTransition = rememberInfiniteTransition(label = "icon animation")
                        val iconScale by iconTransition.animateFloat(
                            initialValue = 0.85f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "icon pulse"
                        )

                        val iconRotation by iconTransition.animateFloat(
                            initialValue = -3f,
                            targetValue = 3f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "icon rotation"
                        )

                        // Get icon resource from OnBoardingItems
                        items.icon?.let { iconResId ->
                            Image(
                                painter = painterResource(id = iconResId),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .graphicsLayer {
                                        scaleX = iconScale
                                        scaleY = iconScale
                                        rotationZ = iconRotation
                                    },
                                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                            )
                        } ?: Icon(
                            // Fallback icon if items.icon is null
                            painter = painterResource(id = android.R.drawable.ic_dialog_info),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .graphicsLayer {
                                    scaleX = iconScale
                                    scaleY = iconScale
                                    rotationZ = iconRotation
                                },
                            tint = Color.White
                        )

                        // Add animated glow effect
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            SecondaryColor.copy(alpha = 0.3f),
                                            Color.Transparent
                                        ),
                                        radius = 80f * iconScale
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Title with enhanced typography and animations
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 300)) +
                            slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    Text(
                        text = stringResource(id = items.title),
                        style = Typography.titleLarge.copy(
                            color = PrimaryColor,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.graphicsLayer {
                            // Add subtle shadow for depth
                            shadowElevation = 2f
                        }
                    )
                }

                // 3. Enhanced separator with more dynamic animation
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 500)) +
                            expandHorizontally(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                expandFrom = Alignment.CenterHorizontally
                            )
                ) {
                    // Animated separator with shine effect
                    val shinePosition = remember { Animatable(-1f) }

                    LaunchedEffect(Unit) {
                        shinePosition.snapTo(-1f)
                        shinePosition.animateTo(
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .width(40.dp)
                            .height(3.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        PrimaryColor.copy(alpha = 0.5f),
                                        PrimaryColor,
                                        PrimaryColor.copy(alpha = 0.5f)
                                    )
                                ),
                                shape = RoundedCornerShape(1.5.dp)
                            )
                    ) {
                        // Animated shine effect
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.6f),
                                            Color.Transparent
                                        ),
                                        startX = -20f + (80f * shinePosition.value),
                                        endX = 20f + (80f * shinePosition.value)
                                    )
                                )
                        )
                    }
                }

                // 4. Description with enhanced typography and staggered character animation
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(800, delayMillis = 700)) +
                            slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                initialOffsetY = { it / 4 }
                            )
                ) {
                    Text(
                        text = stringResource(id = items.description),
                        style = Typography.bodyLarge.copy(
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 5. Button with enhanced animation, design and effects
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 900)) +
                            slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                initialOffsetY = { it / 3 }
                            )
                ) {
                    // Enhanced button animations
                    val buttonScale = remember { Animatable(1f) }
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(currentPage) {
                        // Initial bounce animation when page changes
                        buttonScale.snapTo(0.8f)
                        buttonScale.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    }

                    // Glow effect for button
                    val glowTransition = rememberInfiniteTransition(label = "button glow")
                    val glowAlpha by glowTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 0.6f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "glow alpha"
                    )

                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = buttonScale.value
                                scaleY = buttonScale.value
                            }
                    ) {
                        // Glow effect underneath the button
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .width(180.dp)
                                .height(54.dp)
                                .graphicsLayer {
                                    alpha = glowAlpha
                                    shadowElevation = 20f
                                }
                                .background(
                                    color = PrimaryColor.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                        )

                        Button(
                            onClick = {
                                // Animate button press
                                scope.launch {
                                    buttonScale.animateTo(
                                        targetValue = 0.9f,
                                        animationSpec = tween(100)
                                    )
                                    buttonScale.animateTo(
                                        targetValue = 1f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )
                                    onButtonClick()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryColor
                            ),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 10.dp,
                                hoveredElevation = 8.dp
                            ),
                            modifier = Modifier
                                .height(54.dp)
                                .semantics {
                                    contentDescription =
                                        if (isLastPage) "Get Started" else "Continue to next page"
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Enhanced text with animated shadow
                                Text(
                                    text = if (isLastPage) "Get Started" else "Continue",
                                    style = Typography.labelSmall.copy(
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                // Arrow icon with enhanced animations
                                if (!isLastPage) {
                                    val infiniteTransition =
                                        rememberInfiniteTransition(label = "arrow animation")
                                    val arrowOffset by infiniteTransition.animateFloat(
                                        initialValue = 0f,
                                        targetValue = 8f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(700, easing = FastOutSlowInEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "arrow bounce"
                                    )

                                    val arrowRotation by infiniteTransition.animateFloat(
                                        initialValue = 0f,
                                        targetValue = 10f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1000, easing = FastOutSlowInEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "arrow rotation"
                                    )

                                    Icon(
                                        painter = painterResource(android.R.drawable.ic_media_play),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .graphicsLayer {
                                                translationX = arrowOffset
                                                rotationZ = arrowRotation
                                            }
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

// Custom easing curves for more natural animations
private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
private val EaseInQuad = CubicBezierEasing(0.55f, 0.085f, 0.68f, 0.53f)
private val EaseOutBack = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
private val EaseOutExpo = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

// Helper function for linear interpolation
private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}