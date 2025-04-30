package com.example.primecare.Home.data

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = ""
)

data class Post(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = 0,
    val likeCount: Long = 0
)

data class Like(
    val userId: String = "",
    val timestamp: Long = 0
)

data class Comment(
    val id: String = "",
    val userId: String = "",
    val content: String = "",
    val timestamp: Long = 0
)