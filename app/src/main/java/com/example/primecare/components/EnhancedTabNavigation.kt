package com.example.primecare.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.primecare.R

val TabItems = listOf(
    R.drawable.home to "Home",
    R.drawable.book to "Books",
    R.drawable.movie to "Shows",
    R.drawable.music to "Music",
    R.drawable.settings to "Settings"
)

@Composable
fun EnhancedTabNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Surface that contains the bottom navigation bar
    Surface(
        color = MaterialTheme.colorScheme.surface, // Replace Color.White
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItems.forEachIndexed { index, (iconRes, label) ->
                val isSelected = selectedTab == index

                // Animate color change on tab selection
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, // Replace MainColor and Line
                    animationSpec = tween(400)
                )

                // Animate scale on tab selection
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1f,
                    animationSpec = tween(200)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current,
                            onClick = { onTabSelected(index) }
                        )
                        .semantics { role = Role.Tab }
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .scale(scale)
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent, // Replace MainColor
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = "$label tab",
                            tint = iconColor, // Dynamically change icon color based on selection
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = iconColor,  // Dynamically change text color based on selection
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(4.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)  // Replace MainColor
                        )
                    }
                }
            }
        }
    }
}