package com.example.primecare.WorkOut.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient


object RetrofitClient {
    private const val BASE_URL = "https://exercisedb.p.rapidapi.com/"

    val exerciseApiService: ExerciseApiService by lazy {
        // Create OkHttpClient
        val okHttpClient = OkHttpClient.Builder().build()

        // Build Retrofit instance
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExerciseApiService::class.java)
    }
}