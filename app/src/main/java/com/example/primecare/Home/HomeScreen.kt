package com.example.primecare.Home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.primecare.Home.data.Comment
import com.example.primecare.Home.data.Like
import com.example.primecare.Home.data.Post
import com.example.primecare.Home.data.User
import com.example.primecare.ui.theme.MainColor
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

val SecondaryColor = Color(0xFF6200EA)
val DividerColor = Color(0xFFE0E0E0)
val DividerColorDark = Color(0xFF424242) // Dark divider for dark theme
val ErrorColor = Color(0xFFD32F2F) // For error messages

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogScreen(
    viewModel: BlogViewModel,
    currentUserId: String,
    onNavigateToCreatePost: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posts by remember { derivedStateOf { viewModel.posts } }
    val isLoading by remember { viewModel.isLoading }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isLoading)
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_dialog_info),
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "PrimeCare Community",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToCreatePost,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .shadow(4.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "New Post",
                            tint = MainColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainColor
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.loadData() },
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when {
                    errorMessage != null -> {
                        ErrorMessage(errorMessage!!) {
                            viewModel.loadData()
                        }
                    }
                    isLoading -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(6) {
                                PlaceholderPostCard(isDarkTheme)
                            }
                        }
                    }
                    posts.isEmpty() -> {
                        EmptyStateMessage(onNavigateToCreatePost)
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(posts, key = { it.id }) { post ->
                                PostCard(
                                    post = post,
                                    user = viewModel.users[post.userId],
                                    likes = viewModel.likes[post.id] ?: emptyList(),
                                    comments = viewModel.comments[post.id] ?: emptyList(),
                                    isLiked = viewModel.likes[post.id]?.any { it.userId == currentUserId } == true,
                                    onLike = { viewModel.toggleLike(post.id, currentUserId) },
                                    onComment = { content -> viewModel.addComment(post.id, currentUserId, content) },
                                    users = viewModel.users,
                                    isDarkTheme = isDarkTheme
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PostCard(
    post: Post,
    user: User?,
    likes: List<Like>,
    comments: List<Comment>,
    isLiked: Boolean,
    onLike: () -> Unit,
    onComment: (String) -> Unit,
    users: Map<String, User>,
    isDarkTheme: Boolean
) {
    var showCommentDialog by remember { mutableStateOf(false) }
    var showComments by remember { mutableStateOf(false) }

    // Animation for like button
    val scaleAnim = animateFloatAsState(
        targetValue = if (isLiked) 1.2f else 1.0f,
        animationSpec = tween(100)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with gradient border
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(MainColor, SecondaryColor),
                                start = Offset(0f, 0f),
                                end = Offset(100f, 100f)
                            ),
                            shape = CircleShape
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        GlideImage(
                            model = null, // Replace with user.avatarUrl if available
                            contentDescription = "User Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        ) {
                            it.placeholder(android.R.drawable.ic_menu_report_image)
                                .error(android.R.drawable.ic_menu_report_image)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "${user?.firstName ?: "Unknown"} ${user?.lastName ?: "User"}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatDate(post.timestamp),
                        fontSize = 12.sp,
                        color = if (isDarkTheme) Color(0xFFBDBDBD) else Color(0xFF757575)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post title with accent color
            Text(
                text = post.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MainColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Post content
            Text(
                text = post.content,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.animateContentSize()
            )

            if (post.content.length > 100) {
                TextButton(
                    onClick = { /* TODO: Implement full post view */ },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Read more",
                        color = MainColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = if (isDarkTheme) DividerColorDark else DividerColor)
            Spacer(modifier = Modifier.height(12.dp))

            // Interaction buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like button with animation
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onLike() }
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isLiked) "Unlike" else "Like",
                            tint = if (isLiked) Color.Red else if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF616161),
                            modifier = Modifier
                                .size(22.dp)
                                .scale(scaleAnim.value)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = likes.size.toString(),
                            fontSize = 14.sp,
                            color = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF616161)
                        )
                    }

                    // Comment button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showCommentDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Comment,
                            contentDescription = "Comment",
                            tint = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF616161),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = comments.size.toString(),
                            fontSize = 14.sp,
                            color = if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF616161)
                        )
                    }
                }

                // Show/hide comments button
                TextButton(
                    onClick = { showComments = !showComments },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MainColor
                    )
                ) {
                    Text(
                        text = if (showComments) "Hide comments" else "Show comments",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Comments section
            if (showComments && comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = if (isDarkTheme) DividerColorDark else DividerColor)
                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    comments.forEach { comment ->
                        CommentItem(comment = comment, user = users[comment.userId], isDarkTheme = isDarkTheme)
                    }
                }
            }
        }
    }

    // Comment dialog
    if (showCommentDialog) {
        CommentDialog(
            onDismiss = { showCommentDialog = false },
            onConfirm = { content ->
                if (content.isNotBlank()) {
                    onComment(content)
                }
                showCommentDialog = false
            }
        )
    }
}

@Composable
fun CommentDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Write a comment",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = MainColor,
                        unfocusedIndicatorColor = Color(0xFF9E9E9E)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                    placeholder = {
                        Text(
                            "Share your thoughts...",
                            color = Color(0xFF9E9E9E)
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF757575)
                        )
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onConfirm(commentText) },
                        enabled = commentText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainColor,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFBDBDBD),
                            disabledContentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Post Comment", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CommentItem(comment: Comment, user: User?, isDarkTheme: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Comment author avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MainColor),
            contentAlignment = Alignment.Center
        ) {
            GlideImage(
                model = null, // Replace with user.avatarUrl if available
                contentDescription = "User Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            ) {
                it.placeholder(android.R.drawable.ic_menu_report_image)
                    .error(android.R.drawable.ic_menu_report_image)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Comment content in a card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) Color(0xFF424242) else Color(0xFFF5F5F5),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "${user?.firstName ?: "Unknown"} ${user?.lastName ?: "User"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MainColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = comment.content,
                    fontSize = 14.sp,
                    color = if (isDarkTheme) Color.White else Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatDate(comment.timestamp),
                    fontSize = 12.sp,
                    color = if (isDarkTheme) Color(0xFFBDBDBD) else Color(0xFF757575)
                )
            }
        }
    }
}

@Composable
fun EmptyStateMessage(onNavigateToCreatePost: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_report_image),
                contentDescription = "No Posts",
                modifier = Modifier.size(120.dp),
                tint = Color(0xFF9E9E9E)
            )

            Text(
                text = "No posts yet",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Be the first to share something with the community!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color(0xFF757575)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNavigateToCreatePost,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Post"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Create Post", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_dialog_alert),
                contentDescription = "Error",
                tint = ErrorColor,
                modifier = Modifier.size(60.dp)
            )

            Text(
                text = "Oops!",
                style = MaterialTheme.typography.headlineMedium,
                color = ErrorColor,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.shadow(4.dp, RoundedCornerShape(12.dp))
            ) {
                Text(
                    "Try Again",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PlaceholderPostCard(isDarkTheme: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .shimmer(isDarkTheme)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(16.dp)
                            .shimmer(isDarkTheme)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(12.dp)
                            .shimmer(isDarkTheme)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .shimmer(isDarkTheme)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .shimmer(isDarkTheme)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
                    .shimmer(isDarkTheme)
            )
        }
    }
}

@Composable
fun Modifier.shimmer(isDarkTheme: Boolean = true): Modifier {
    var offset by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            offset += 0.02f
            if (offset > 1.2f) offset = -0.2f
            delay(16)
        }
    }

    val shimmerColors = if (isDarkTheme) {
        listOf(
            Color(0xFF424242),
            Color(0xFF616161),
            Color(0xFF424242)
        )
    } else {
        listOf(
            Color(0xFFE0E0E0),
            Color(0xFFF5F5F5),
            Color(0xFFE0E0E0)
        )
    }

    return this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(offset * 1000f, 0f),
            end = Offset((offset + 0.4f) * 1000f, 0f)
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault())
    return formatter.format(Date(timestamp))
}