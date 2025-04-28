//package com.example.primecare.WorkOut
//
//import android.graphics.drawable.Drawable
//import android.util.Log
//import android.widget.ImageView
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.media3.common.MediaItem
//import androidx.media3.common.PlaybackException
//import androidx.media3.common.Player
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.ui.PlayerView
//import androidx.navigation.NavController
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.DataSource
//import com.bumptech.glide.load.engine.GlideException
//import com.bumptech.glide.request.RequestListener
//import com.bumptech.glide.request.target.Target
//import com.example.primecare.WorkOut.api.Exercise
//
//@Composable
//fun ExerciseDetailScreen(
//    exerciseId: String,
//    modifier: Modifier = Modifier,
//    viewModel: WorkOutViewModel = viewModel(),
//    navController: NavController
//) {
//    val workOut = viewModel.workOut.collectAsState().value
//    val exercise = workOut?.exercises?.find { it.id == exerciseId }
//
//    LaunchedEffect(exerciseId, workOut) {
//        if (workOut == null) {
//            viewModel.fetchExerciseIds(page = 1, limit = 10)
//        }
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .background(Color.Black)
//            .padding(16.dp)
//    ) {
//        exercise?.let {
//            Text(
//                text = it.displayName,
//                color = Color.White,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(Modifier.height(8.dp))
//            Text("ID: ${it.id}", color = Color.LightGray, fontSize = 16.sp)
//            Spacer(Modifier.height(4.dp))
//            Text("Aliases: ${it.aliases.joinToString()}", color = Color.LightGray, fontSize = 16.sp)
//            Spacer(Modifier.height(8.dp))
//            Text("Images: ${it.premiumImages.size}", color = Color.LightGray, fontSize = 16.sp)
//            Spacer(Modifier.height(8.dp))
//
//            if (it.premiumImages.isNotEmpty()) {
//                AndroidView(
//                    factory = { ctx ->
//                        ImageView(ctx).apply { scaleType = ImageView.ScaleType.CENTER_CROP }
//                    },
//                    update = { imageView ->
//                        Glide.with(imageView.context)
//                            .load(it.premiumImages.first())
//                            .error(android.R.drawable.ic_menu_close_clear_cancel)
//                            .into(imageView)
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                )
//                Spacer(Modifier.height(8.dp))
//            } else {
//                Text("No images available", color = Color.White, fontSize = 16.sp)
//                Spacer(Modifier.height(8.dp))
//            }
//
//            if (it.premiumVideos.isNotEmpty()) {
//                Text(
//                    text = "Video Tutorial",
//                    color = Color.White,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Medium
//                )
//                Spacer(Modifier.height(8.dp))
//                VideoPlayer(
//                    videoUrl = it.premiumVideos.first(),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                )
//                Spacer(Modifier.height(8.dp))
//            } else {
//                Text("No video tutorial available", color = Color.White, fontSize = 16.sp)
//                Spacer(Modifier.height(8.dp))
//            }
//
//            Button(
//                onClick = { navController.popBackStack() },
//                modifier = Modifier.align(Alignment.End)
//            ) {
//                Text("Back")
//            }
//        } ?: Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(
//                text = workOut?.let { "Exercise not found" } ?: "Loading data...",
//                color = if (workOut == null) Color.Yellow else Color.Red,
//                fontSize = 16.sp
//            )
//            if (workOut == null) {
//                Spacer(Modifier.height(8.dp))
//                Button(onClick = { viewModel.fetchExerciseIds(page = 1, limit = 10) }) {
//                    Text("Retry")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun VideoPlayer(
//    videoUrl: String,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//    val exoPlayer = remember {
//        ExoPlayer.Builder(context).build().apply {
//            setMediaItem(MediaItem.fromUri(videoUrl))
//            addListener(object : Player.Listener {
//                override fun onPlayerError(error: PlaybackException) {
//                    Log.e("ExoPlayer", "Playback error: ${error.message}")
//                }
//            })
//            prepare()
//            playWhenReady = true
//        }
//    }
//
//    DisposableEffect(Unit) {
//        onDispose { exoPlayer.release() }
//    }
//
//    AndroidView(
//        factory = { ctx ->
//            PlayerView(ctx).apply { player = exoPlayer }
//        },
//        modifier = modifier
//    )
//}
