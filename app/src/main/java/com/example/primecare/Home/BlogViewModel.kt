package com.example.primecare.Home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.primecare.Home.data.Comment
import com.example.primecare.Home.data.Like
import com.example.primecare.Home.data.Post
import com.example.primecare.Home.data.User as FirestoreUser
import com.example.primecare.Room.AppDB
import com.example.primecare.Room.User as RoomUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BlogViewModel(context: Context, private val currentUserId: String) : ViewModel() {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val POSTS_COLLECTION = "posts"
        private const val LIKES_COLLECTION = "likes"
        private const val COMMENTS_COLLECTION = "comments"
    }

    private val db: FirebaseFirestore = Firebase.firestore
    private val userDao = AppDB.getDatabase(context).userDao()

    // UI state
    val posts = mutableStateListOf<Post>()
    val users = mutableStateMapOf<String, FirestoreUser>()
    val likes = mutableStateMapOf<String, List<Like>>()
    val comments = mutableStateMapOf<String, List<Comment>>()
    val isLoading = mutableStateOf(false)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val listenerRegistrations = mutableListOf<ListenerRegistration>()

    init {
        if (currentUserId.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    val existingRoomUser = userDao.getUserByFirebaseId(currentUserId)
                    if (existingRoomUser == null) {
                        val userDoc = db.collection(USERS_COLLECTION).document(currentUserId).get().await()
                        val roomUser = if (userDoc.exists()) {
                            RoomUser(
                                firebaseId = currentUserId,
                                firstName = userDoc.getString("firstName") ?: "",
                                lastName = userDoc.getString("lastName") ?: ""
                            )
                        } else {
                            RoomUser(firebaseId = currentUserId, firstName = "Anonymous", lastName = "User")
                        }
                        userDao.insert(roomUser)
                    }
                } catch (e: Exception) {
                    Log.e("BlogViewModel", "Failed to sync current user to Room: ${e.message}", e)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistrations.forEach { it.remove() }
    }

    // Function to set error message
    fun setError(message: String) {
        _errorMessage.value = message
    }
    fun loadData() {
        isLoading.value = true

        val postListener = db.collection(POSTS_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _errorMessage.value = "Failed to load posts: ${error.message}"
                    }
                    Log.e("BlogViewModel", "Posts error: ${error.message}", error)
                    isLoading.value = false
                    return@addSnapshotListener
                }

                val postList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Post(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            title = doc.getString("title") ?: "",
                            content = doc.getString("content") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0,
                            likeCount = doc.getLong("likeCount") ?: 0
                        )
                    } catch (e: Exception) {
                        Log.e("BlogViewModel", "Post parse error: ${e.message}", e)
                        null
                    }
                } ?: emptyList()

                posts.clear()
                posts.addAll(postList)

                postList.forEach { post ->
                    if (post.userId.isNotEmpty() && users[post.userId] == null) {
                        viewModelScope.launch {
                            try {
                                val userDoc = db.collection(USERS_COLLECTION).document(post.userId).get().await()
                                if (userDoc.exists()) {
                                    val firestoreUser = FirestoreUser(
                                        id = post.userId,
                                        firstName = userDoc.getString("firstName") ?: "",
                                        lastName = userDoc.getString("lastName") ?: ""
                                    )
                                    users[post.userId] = firestoreUser

                                    if (userDao.getUserByFirebaseId(post.userId) == null) {
                                        userDao.insert(
                                            RoomUser(
                                                firebaseId = post.userId,
                                                firstName = firestoreUser.firstName,
                                                lastName = firestoreUser.lastName
                                            )
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                viewModelScope.launch {
                                    _errorMessage.value = "Failed to load user ${post.userId}: ${e.message}"
                                }
                            }
                        }
                    }

                    // Likes listener
                    val likeReg = db.collection(POSTS_COLLECTION).document(post.id).collection(LIKES_COLLECTION)
                        .addSnapshotListener { likeSnapshot, likeError ->
                            if (likeError != null) {
                                viewModelScope.launch {
                                    _errorMessage.value = "Failed to load likes for post ${post.id}: ${likeError.message}"
                                }
                                return@addSnapshotListener
                            }
                            val likeList = likeSnapshot?.documents?.map {
                                Like(userId = it.id, timestamp = it.getLong("timestamp") ?: 0)
                            } ?: emptyList()
                            likes[post.id] = likeList
                        }
                    listenerRegistrations.add(likeReg)

                    // Comments listener
                    val commentReg = db.collection(POSTS_COLLECTION).document(post.id).collection(COMMENTS_COLLECTION)
                        .orderBy("timestamp")
                        .addSnapshotListener { commentSnapshot, commentError ->
                            if (commentError != null) {
                                viewModelScope.launch {
                                    _errorMessage.value = "Failed to load comments for post ${post.id}: ${commentError.message}"
                                }
                                return@addSnapshotListener
                            }
                            val commentList = commentSnapshot?.documents?.mapNotNull {
                                try {
                                    Comment(
                                        id = it.id,
                                        userId = it.getString("userId") ?: "",
                                        content = it.getString("content") ?: "",
                                        timestamp = it.getLong("timestamp") ?: 0
                                    )
                                } catch (e: Exception) {
                                    Log.e("BlogViewModel", "Comment parse error: ${e.message}", e)
                                    null
                                }
                            } ?: emptyList()
                            comments[post.id] = commentList
                        }
                    listenerRegistrations.add(commentReg)
                }

                isLoading.value = false
            }

        listenerRegistrations.add(postListener)
    }

    fun createPost(userId: String, title: String, content: String, onSuccess: () -> Unit) {
        if (userId.isEmpty()) {
            viewModelScope.launch { _errorMessage.value = "User not authenticated. Please log in." }
            return
        }

        viewModelScope.launch {
            try {
                val post = hashMapOf(
                    "userId" to userId,
                    "title" to title,
                    "content" to content,
                    "timestamp" to System.currentTimeMillis(),
                    "likeCount" to 0
                )
                db.collection(POSTS_COLLECTION).add(post).await()
                onSuccess()
            } catch (e: FirebaseFirestoreException) {
                _errorMessage.value = when (e.code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Permission denied: Please log in to create a post."
                    else -> "Failed to create post: ${e.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create post: ${e.message}"
            }
        }
    }

    fun toggleLike(postId: String, userId: String) {
        if (userId.isEmpty()) {
            viewModelScope.launch { _errorMessage.value = "User not authenticated. Please log in." }
            return
        }

        viewModelScope.launch {
            try {
                val likeRef = db.collection(POSTS_COLLECTION).document(postId).collection(LIKES_COLLECTION).document(userId)
                val likeDoc = likeRef.get().await()
                val postRef = db.collection(POSTS_COLLECTION).document(postId)

                db.runTransaction { transaction ->
                    val currentLikeCount = (transaction.get(postRef).getLong("likeCount") ?: 0).toInt()
                    if (likeDoc.exists()) {
                        transaction.delete(likeRef)
                        transaction.update(postRef, "likeCount", (currentLikeCount - 1).coerceAtLeast(0))
                    } else {
                        transaction.set(likeRef, mapOf("userId" to userId, "timestamp" to System.currentTimeMillis()))
                        transaction.update(postRef, "likeCount", currentLikeCount + 1)
                    }
                }.await()
            } catch (e: FirebaseFirestoreException) {
                _errorMessage.value = when (e.code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Permission denied: Please log in to like this post."
                    else -> "Failed to toggle like: ${e.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to toggle like: ${e.message}"
            }
        }
    }

    fun addComment(postId: String, userId: String, content: String) {
        if (userId.isEmpty()) {
            viewModelScope.launch { _errorMessage.value = "User not authenticated. Please log in." }
            return
        }

        viewModelScope.launch {
            try {
                val comment = hashMapOf(
                    "userId" to userId,
                    "content" to content,
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection(POSTS_COLLECTION).document(postId).collection(COMMENTS_COLLECTION).add(comment).await()
            } catch (e: FirebaseFirestoreException) {
                _errorMessage.value = when (e.code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Permission denied: Please log in to comment."
                    else -> "Failed to add comment: ${e.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add comment: ${e.message}"
            }
        }
    }

    suspend fun getCurrentRoomUser(): RoomUser? {
        return userDao.getFirstUser()
    }
}
