package com.example.primecare.Meals.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MealApiService {
    // Fetch a list of meals with pagination, query, category filters, and API key
    @GET("recipes/complexSearch")
    suspend fun getMeals(
        @Query("query") query: String,
        @Query("number") number: Int,
        @Query("offset") offset: Int,
        @Query("cuisine") cuisine: String? = null, // Filter by cuisine (e.g., Italian, Mexican)
        @Query("type") type: String? = null,      // Filter by dish type (e.g., Dessert, Main Course)
        @Query("diet") diet: String? = null,      // Filter by diet (e.g., Vegan, Gluten-Free)
        @Query("apiKey") apiKey: String
    ): Meals

    // Fetch detailed information for a specific meal by ID with API key
    @GET("recipes/{id}/information")
    suspend fun getMealInfo(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): Info
}