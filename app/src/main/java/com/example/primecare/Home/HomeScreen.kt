package com.example.primecare.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.primecare.Home.data.User
import com.example.primecare.Home.data.Post
import com.example.primecare.Home.data.Like
import com.example.primecare.Home.data.Comment
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BlogScreen(
    viewModel: BlogViewModel,
    currentUserId: String,
    onNavigateToCreatePost: () -> Unit,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PrimeCare Community",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = onNavigateToCreatePost,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.End)
        ) {
            Text("New Post")
        }

        val isLoading by remember { viewModel.isLoading }
        val errorMessage by viewModel.errorMessage.collectAsState()

        when {
            isLoading -> LoadingIndicator()
            errorMessage != null -> ErrorMessage(errorMessage!!)
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.posts) { post ->
                        PostCard(
                            post = post,
                            user = viewModel.users[post.userId],
                            likes = viewModel.likes[post.id] ?: emptyList(),
                            comments = viewModel.comments[post.id] ?: emptyList(),
                            isLiked = viewModel.likes[post.id]?.any { it.userId == currentUserId } == true,
                            onLike = { viewModel.toggleLike(post.id, currentUserId) },
                            onComment = { content -> viewModel.addComment(post.id, currentUserId, content) },
                            users = viewModel.users
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    user: User?,
    likes: List<Like>,
    comments: List<Comment>,
    isLiked: Boolean,
    onLike: () -> Unit,
    onComment: (String) -> Unit,
    users: Map<String, User>
) {
    val commentText = remember(post.id) { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = post.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = "by ${user?.firstName ?: "Unknown"} ${user?.lastName ?: "User"}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.content,
                fontSize = 14.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatDate(post.timestamp),
                fontSize = 12.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${likes.size} Likes", fontSize = 14.sp, color = Color.Gray)
                Text("${comments.size} Comments", fontSize = 14.sp, color = Color.Gray)
            }

            Button(
                onClick = onLike,
                modifier = Modifier.padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLiked) Color.Gray else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isLiked) "Unlike" else "Like")
            }

            TextField(
                value = commentText.value,
                onValueChange = { commentText.value = it },
                label = { Text("Write a comment...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )

            Button(
                onClick = {
                    if (commentText.value.isNotBlank()) {
                        onComment(commentText.value)
                        commentText.value = ""
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                Text("Comment")
            }

            if (comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Comments:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                comments.forEach { comment ->
                    CommentItem(comment = comment, user = users[comment.userId])
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment, user: User?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "${user?.firstName ?: "Unknown"} ${user?.lastName ?: "User"}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        Text(
            text = comment.content,
            fontSize = 12.sp,
            color = Color.Black
        )
        Text(
            text = formatDate(comment.timestamp),
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = Color.Red, fontSize = 16.sp)
    }
}

fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
