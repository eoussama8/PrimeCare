package com.example.primecare.WorkOut.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Header
import retrofit2.Response

interface ExerciseApiService {
    @GET("exercises")
    suspend fun getExercises(
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") apiHost: String
    ): Response<List<Exercise>>

    @GET("exercises/name/{name}")
    suspend fun searchExercises(
        @Path("name") name: String,
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") apiHost: String
    ): Response<List<Exercise>>

    @GET("exercises/bodyPart/{bodyPart}")
    suspend fun getExercisesByBodyPart(
        @Path("bodyPart") bodyPart: String,
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") apiHost: String
    ): Response<List<Exercise>>

    @GET("exercises/bodyPartList")
    suspend fun getBodyParts(
        @Header("x-rapidapi-key") apiKey: String,
        @Header("x-rapidapi-host") apiHost: String
    ): Response<List<String>>
}