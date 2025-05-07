package com.example.primecare.OnBoarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.primecare.R
import com.example.primecare.ui.theme.Typography
import com.example.primecare.ui.theme.MainColor
import com.example.primecare.ui.theme.White
import com.example.primecare.ui.theme.Black
import kotlinx.coroutines.launch

@Composable
fun OnBoarding(onFinish: () -> Unit = {}) {
    val items = OnBoardingItems.getData()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { items.size })
    val currentPage = pagerState.currentPage

    // Enhanced animations
    val pageTransition = rememberInfiniteTransition(label = "pageTransition")
    val pulseAnimation = pageTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Floating particles animation
    val particleTransition = rememberInfiniteTransition(label = "particleTransition")
    val particleOffset = particleTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleFloat"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Onboarding Pager with improved transition
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSpacing = 0.dp,
        ) { page ->
            OnBoardingPage(
                item = items[page],
                isLastPage = page == items.size - 1,
                onGetStartedClick = onFinish
            )
        }

        // Floating particles effect
        Canvas(modifier = Modifier
            .fillMaxSize()
            .alpha(0.3f)
            .zIndex(1f)
        ) {
            repeat(8) { i ->
                val x = size.width * (0.2f + (i * 0.1f) + particleOffset.value * 0.02f)
                val y = size.height * (0.3f + (i * 0.1f * particleOffset.value))
                val particleSize = 5.dp.toPx() + (i % 3) * 2.dp.toPx()

                drawCircle(
                    color = White.copy(alpha = 0.3f - (i * 0.02f)),
                    radius = particleSize,
                    center = Offset(x, y)
                )
            }
        }

        // App Logo with subtle glow effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp)
                .height(48.dp)
                .align(Alignment.TopCenter)
                .zIndex(3f)
        ) {
            // Logo glow effect
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
                    .scale(pulseAnimation.value * 0.7f + 0.5f)
                    .alpha(0.2f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MainColor.copy(alpha = 0.6f),
                                MainColor.copy(alpha = 0f)
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Logo text with enhanced styling
            Text(
                text = "PrimeCare",
                style = Typography.headlineMedium.copy(
                    color = White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    letterSpacing = 0.5.sp,
                    shadow = Shadow(
                        color = MainColor.copy(alpha = 0.5f),
                        offset = Offset(0f, 2f),
                        blurRadius = 4f
                    )
                ),
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }

        // Top navigation area with improved skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, end = 20.dp)
                .zIndex(2f),
            horizontalArrangement = Arrangement.End
        ) {
            // Skip button with enhanced visual feedback
            if (currentPage < items.size - 1) {
                val buttonHover = remember { mutableStateOf(false) }
                TextButton(
                    onClick = { onFinish() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = White
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .width(88.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MainColor.copy(alpha = 0.5f),
                                    MainColor.copy(alpha = 0.3f)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(88f, 40f)
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    White.copy(alpha = 0.8f),
                                    White.copy(alpha = 0.2f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .graphicsLayer {
                            shadowElevation = 8f
                            shape = RoundedCornerShape(20.dp)
                        }
                ) {
                    Text(
                        text = stringResource(R.string.skip),
                        style = Typography.labelMedium.copy(
                            color = White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        }

        // Enhanced page indicator with animation
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 140.dp)
                .align(Alignment.BottomCenter)
                .zIndex(2f)
        ) {
            repeat(items.size) { position ->
                val isSelected = position == currentPage
                val indicatorSize = if (isSelected) 16.dp else 8.dp
                val indicatorColor = if (isSelected) {
                    MainColor
                } else {
                    White.copy(alpha = 0.6f)
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(indicatorSize)
                        .clip(CircleShape)
                        .background(indicatorColor)
                        .then(
                            if (isSelected) {
                                Modifier
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                White.copy(alpha = 0.9f),
                                                MainColor.copy(alpha = 0.2f)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .scale(pulseAnimation.value)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = CircleShape,
                                        spotColor = MainColor
                                    )
                            } else {
                                Modifier
                            }
                        )
                ) {
                    // Inner glow for selected indicator
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .background(
                                    White.copy(alpha = 0.8f)
                                )
                        )
                    }
                }
            }
        }

        // Navigation buttons with floating effect and improved layout
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 36.dp)
                .align(Alignment.BottomCenter)
                .height(60.dp)
                .zIndex(2f)
        ) {
            // Previous button with enhanced styling
            if (currentPage > 0) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                currentPage - 1,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    },
                    border = BorderStroke(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(White.copy(alpha = 0.8f), MainColor.copy(alpha = 0.3f))
                        )
                    ),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = White
                    ),
                    modifier = Modifier
                        .width(130.dp)
                        .height(60.dp)
                        .graphicsLayer {
                            shadowElevation = 8f
                            shape = RoundedCornerShape(30.dp)
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Previous",
                        modifier = Modifier.size(20.dp),
                        tint = White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Previous",
                        style = Typography.labelLarge.copy(
                            color = White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            } else {
                // Empty placeholder with improved layout
                Spacer(modifier = Modifier.width(130.dp))
            }

            // Next button with enhanced styling and effects
            if (currentPage < items.size - 1) {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                currentPage + 1,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor
                    ),
                    shape = RoundedCornerShape(30.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 10.dp
                    ),
                    modifier = Modifier
                        .width(130.dp)
                        .height(60.dp)
                        .graphicsLayer {
                            shadowElevation = 12f
                            shape = RoundedCornerShape(30.dp)
                        }
                ) {
                    Text(
                        text = "Next",
                        style = Typography.labelLarge.copy(
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.right),
                        contentDescription = "Next",
                        modifier = Modifier.size(20.dp),
                        tint = White
                    )
                }
            }
        }
    }
}

@Composable
fun OnBoardingPage(
    item: OnBoardingItems,
    isLastPage: Boolean,
    onGetStartedClick: () -> Unit
) {
    // Enhanced animation states with improved timing
    val imageScale = remember { Animatable(0.85f) }
    val contentAlpha = remember { Animatable(0f) }
    val contentSlide = remember { Animatable(50f) }

    LaunchedEffect(key1 = item.title) {
        // Reset animations when page changes
        imageScale.snapTo(0.85f)
        contentAlpha.snapTo(0f)
        contentSlide.snapTo(50f)

        // Start animations with sequential timing
        imageScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = EaseOutQuart)
        )
        contentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, delayMillis = 200)
        )
        contentSlide.animateTo(
            targetValue = 0f,
            animationSpec = tween(1000, delayMillis = 200, easing = EaseOutQuint)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full screen background image with enhanced scale animation
        Image(
            painter = painterResource(id = item.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = imageScale.value
                    scaleY = imageScale.value
                    alpha = contentAlpha.value
                }
        )

        // Enhanced gradient overlay with better blending
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MainColor.copy(alpha = 0.02f),
                            MainColor.copy(alpha = 0.10f),
                            MainColor.copy(alpha = 0.25f),
                            MainColor.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.80f)
                        ),
                        startY = 100f
                    )
                )
        )

        // Enhanced decorative elements
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = contentAlpha.value * 0.8f
                }
        ) {
            // Multiple decorative arcs for depth
            drawArc(
                color = MainColor.copy(alpha = 0.15f),
                startAngle = 270f,
                sweepAngle = 120f,
                useCenter = false,
                style = Stroke(width = 140f),
                size = Size(size.width * 1.6f, size.height * 0.7f),
                topLeft = Offset(-size.width * 0.3f, size.height * 0.5f)
            )

            drawArc(
                color = White.copy(alpha = 0.05f),
                startAngle = 260f,
                sweepAngle = 140f,
                useCenter = false,
                style = Stroke(width = 80f),
                size = Size(size.width * 1.2f, size.height * 0.5f),
                topLeft = Offset(-size.width * 0.1f, size.height * 0.55f)
            )

            // Additional accent elements
            drawCircle(
                color = MainColor.copy(alpha = 0.08f),
                radius = size.width * 0.4f,
                center = Offset(size.width * 0.8f, size.height * 0.2f)
            )
        }

        // Enhanced content area with improved animations and styling
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 28.dp)
                .padding(bottom = if (isLastPage) 120.dp else 160.dp)
                .graphicsLayer {
                    alpha = contentAlpha.value
                    translationY = contentSlide.value
                }
        ) {
            // Enhanced title with better typography and animation
            Text(
                text = stringResource(id = item.title),
                style = Typography.headlineLarge.copy(
                    color = White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    lineHeight = 42.sp,
                    letterSpacing = (-0.5).sp,
                    shadow = Shadow(
                        color = Black.copy(alpha = 0.5f),
                        offset = Offset(0f, 2f),
                        blurRadius = 4f
                    )
                ),
                modifier = Modifier.padding(bottom = 22.dp)
            )

            // Enhanced description with better readability
            Text(
                text = stringResource(id = item.description),
                style = Typography.bodyLarge.copy(
                    color = White.copy(alpha = 0.95f),
                    lineHeight = 28.sp,
                    fontSize = 18.sp,
                    letterSpacing = 0.25.sp,
                    shadow = Shadow(
                        color = Black.copy(alpha = 0.3f),
                        offset = Offset(0f, 1f),
                        blurRadius = 2f
                    )
                ),
                modifier = Modifier
                    .padding(bottom = 38.dp)
                    .fillMaxWidth(0.95f)
            )

            // Enhanced Get Started button with better visual effects
            if (isLastPage) {
                // Custom pulsating animation for the button
                val buttonTransition = rememberInfiniteTransition(label = "buttonEffects")
                val buttonScale = buttonTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = EaseInOutQuad),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "buttonPulse"
                )

                val glowAlpha = buttonTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 0.4f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = EaseInOutQuad),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "glowPulse"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .graphicsLayer {
                            scaleX = buttonScale.value
                            scaleY = buttonScale.value
                        }
                ) {
                    // Enhanced button glow effect
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .offset(y = 4.dp)
                            .scale(1.1f)
                            .alpha(glowAlpha.value)
                            .clip(RoundedCornerShape(32.dp))
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MainColor.copy(alpha = 0.7f),
                                        MainColor.copy(alpha = 0f)
                                    )
                                )
                            )
                    )

                    // Improved button with better elevation
                    Button(
                        onClick = onGetStartedClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainColor
                        ),
                        shape = RoundedCornerShape(32.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 10.dp,
                            pressedElevation = 16.dp
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(32.dp))
                    ) {
                        Text(
                            text = stringResource(R.string.get_started),
                            style = Typography.labelLarge.copy(
                                color = White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                letterSpacing = 0.5.sp
                            )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        // Enhanced arrow icon with subtle animation
                        Icon(
                            painter = painterResource(id = R.drawable.right),
                            contentDescription = "Get Started",
                            tint = White,
                            modifier = Modifier
                                .size(22.dp)
                                .graphicsLayer {
                                    scaleX = 1.1f
                                    scaleY = 1.1f
                                }
                        )
                    }
                }
            }
        }
    }
}