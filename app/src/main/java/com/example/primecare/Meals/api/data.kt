package com.example.primecare.Meals.api

import com.google.gson.annotations.SerializedName

// Represents the API response for a list of meals.
data class Meals(
    val number: Int,
    val offset: Int,
    val results: List<Result>,
    val totalResults: Int
)

// Represents each individual meal in the list.
data class Result(
    val id: Int,
    val image: String,
    val imageType: String,
    val title: String
)

// Represents detailed information about a specific meal.
data class Info(
    @SerializedName("aggregateLikes") val aggregateLikes: Int,
    @SerializedName("analyzedInstructions") val analyzedInstructions: List<AnalyzedInstruction>,
    @SerializedName("cheap") val cheap: Boolean,
    @SerializedName("cookingMinutes") val cookingMinutes: Int,
    @SerializedName("creditsText") val creditsText: String,
    @SerializedName("cuisines") val cuisines: List<String>,
    @SerializedName("dairyFree") val dairyFree: Boolean,
    @SerializedName("diets") val diets: List<String>,
    @SerializedName("dishTypes") val dishTypes: List<String>,
    @SerializedName("extendedIngredients") val extendedIngredients: List<ExtendedIngredient>,
    @SerializedName("gaps") val gaps: String,
    @SerializedName("glutenFree") val glutenFree: Boolean,
    @SerializedName("healthScore") val healthScore: Double,
    @SerializedName("id") val id: Int,
    @SerializedName("image") val image: String,
    @SerializedName("imageType") val imageType: String,
    @SerializedName("instructions") val instructions: String?,
    @SerializedName("license") val license: String,
    @SerializedName("lowFodmap") val lowFodmap: Boolean,
    @SerializedName("occasions") val occasions: List<String>,
    @SerializedName("originalId") val originalId: Any?,
    @SerializedName("preparationMinutes") val preparationMinutes: Int,
    @SerializedName("pricePerServing") val pricePerServing: Double,
    @SerializedName("readyInMinutes") val readyInMinutes: Int,
    @SerializedName("servings") val servings: Int,
    @SerializedName("sourceName") val sourceName: String,
    @SerializedName("sourceUrl") val sourceUrl: String,
    @SerializedName("spoonacularScore") val spoonacularScore: Double,
    @SerializedName("spoonacularSourceUrl") val spoonacularSourceUrl: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("sustainable") val sustainable: Boolean,
    @SerializedName("title") val title: String,
    @SerializedName("vegan") val vegan: Boolean,
    @SerializedName("vegetarian") val vegetarian: Boolean,
    @SerializedName("veryHealthy") val veryHealthy: Boolean,
    @SerializedName("veryPopular") val veryPopular: Boolean,
    @SerializedName("weightWatcherSmartPoints") val weightWatcherSmartPoints: Int
)

// Represents an analyzed instruction set.
data class AnalyzedInstruction(
    @SerializedName("name") val name: String,
    @SerializedName("steps") val steps: List<InstructionStep>
)

// Represents a single step in the instructions.
data class InstructionStep(
    @SerializedName("number") val number: Int,
    @SerializedName("step") val step: String
)

// Represents an extended ingredient in a meal.
data class ExtendedIngredient(
    @SerializedName("aisle") val aisle: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("consistency") val consistency: String,
    @SerializedName("id") val id: Int,
    @SerializedName("image") val image: String,
    @SerializedName("measures") val measures: Measures,
    @SerializedName("meta") val meta: List<String>,
    @SerializedName("name") val name: String,
    @SerializedName("nameClean") val nameClean: String,
    @SerializedName("original") val original: String,
    @SerializedName("originalName") val originalName: String,
    @SerializedName("unit") val unit: String
)

// Represents the measurement details for ingredients.
data class Measures(
    @SerializedName("metric") val metric: Metric,
    @SerializedName("us") val us: Us
)

// Represents metric measurements (e.g., grams, liters).
data class Metric(
    @SerializedName("amount") val amount: Double,
    @SerializedName("unitLong") val unitLong: String,
    @SerializedName("unitShort") val unitShort: String
)

// Represents measurements in US standard units (e.g., cups, ounces).
data class Us(
    @SerializedName("amount") val amount: Double,
    @SerializedName("unitLong") val unitLong: String,
    @SerializedName("unitShort") val unitShort: String
)