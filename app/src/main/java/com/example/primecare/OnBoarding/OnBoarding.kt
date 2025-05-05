package com.example.primecare.OnBoarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import kotlin.math.absoluteValue

@Composable
fun OnBoarding(onFinish: () -> Unit = {}) {
    val items = OnBoardingItems.getData()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { items.size })
    val currentPage = pagerState.currentPage

    Box(modifier = Modifier.fillMaxSize()) {
        // Onboarding Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val absPageOffset = pageOffset.absoluteValue

            // Simple parallax effect
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

                // Gradient overlay for better text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                )

                // Onboarding content directly on the image
                OnBoardingContent(
                    items = items[page],
                    isLastPage = page == items.size - 1,
                    onButtonClick = {
                        if (page == items.size - 1) {
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(page + 1)
                            }
                        }
                    }
                )
            }
        }

        // Top bar with skip button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .zIndex(2f)
        ) {
            // Simple progress text
            Text(
                text = "${currentPage + 1}/${items.size}",
                style = Typography.labelSmall.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                modifier = Modifier.align(Alignment.CenterStart)
            )

            // Skip button
            Button(
                onClick = { onFinish() },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainColor,
                    contentColor = White
                ),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .semantics { contentDescription = "Skip onboarding" }
            ) {
                Text(
                    text = "Skip",
                    style = Typography.labelSmall.copy(
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
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
                    isSelected = position == currentPage
                )
            }
        }
    }
}

@Composable
fun PageIndicator(isSelected: Boolean) {
    // Simple page indicator
    val width by animateFloatAsState(
        targetValue = if (isSelected) 24f else 8f,
        animationSpec = tween(300),
        label = "indicator width"
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .width(width.dp)
            .height(8.dp)
            .clip(CircleShape)
            .background(if (isSelected) MainColor else White.copy(alpha = 0.5f))
    )
}

@Composable
fun OnBoardingContent(
    items: OnBoardingItems,
    isLastPage: Boolean,
    onButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Icon (optional)
            items.icon?.let { iconResId ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(600)) +
                            slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                ),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MainColor.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconResId),
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(600, delayMillis = 300)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 }
                        )
            ) {
                Text(
                    text = stringResource(id = items.title),
                    style = Typography.titleLarge.copy(
                        color = White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(600, delayMillis = 500)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 }
                        )
            ) {
                Text(
                    text = stringResource(id = items.description),
                    style = Typography.bodyLarge.copy(
                        color = White,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CTA button
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(600, delayMillis = 700)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 }
                        )
            ) {
                Button(
                    onClick = onButtonClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(0.8f)
                        .semantics {
                            contentDescription = if (isLastPage) "Get Started" else "Next"
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isLastPage) "GET STARTED" else "CONTINUE",
                            style = Typography.labelLarge.copy(
                                color = White,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        if (!isLastPage) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_media_play),
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Data class for onboarding items
