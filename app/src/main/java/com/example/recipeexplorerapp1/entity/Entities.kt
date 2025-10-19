package com.example.recipeexplorerapp1.entity

import com.google.gson.annotations.SerializedName

data class RecipeListing(
    @SerializedName("recipes")
    val data: List<RecipeListingData>?,
    @SerializedName("total")
    val totalSO: Int = 0,
    @SerializedName("skip")
    val skip: Int = 0,
    @SerializedName("limit")
    val limit: Int = 0
)

data class RecipeListingData(
    @SerializedName("id")
    val id: Int = 1,

    @SerializedName("name")
    val title: String = "",

    @SerializedName("image")
    val imageUrl: String = "",

    @SerializedName("ingredients")
    val ingredients: List<String>? = null,

    @SerializedName("instructions")
    val instructions: List<String>? = null,

    @SerializedName("prepTimeMinutes")
    val prepTimeMinutes: Int = 0,

    @SerializedName("cookTimeMinutes")
    val cookTimeMinutes: Int = 0,

    @SerializedName("servings")
    val servings: Int = 0,

    @SerializedName("difficulty")
    val difficulty: String = "",

    @SerializedName("cuisine")
    val cuisine: String = "",

    @SerializedName("caloriesPerServing")
    val caloriesPerServing: Int = 0,

    @SerializedName("rating")
    val rating: Double = 0.0,

    @SerializedName("reviewCount")
    val reviewCount: Int = 0,

    @SerializedName("tags")
    val tags: List<String>? = null,

    @SerializedName("mealType")
    val mealType: List<String>? = null,

    @SerializedName("userId")
    val userId: Int = 0
)