package com.example.primecare.WorkOut.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header
import retrofit2.Response

interface ExerciseApiService {
    @GET("exercises")
    suspend fun getExercises(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") apiHost: String
    ): Response<List<Exercise>>

    @GET("exercises")
    suspend fun searchExercises(
        @Query("name") name: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") apiHost: String
    ): Response<List<Exercise>>

    @GET("exercises")
    suspend fun getExercisesByBodyPart(
        @Query("bodyPart") bodyPart: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") apiHost: String
    ): Response<List<Exercise>>

    @GET("exercises/bodyPartList")
    suspend fun getBodyParts(
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") apiHost: String
    ): Response<List<String>>
}