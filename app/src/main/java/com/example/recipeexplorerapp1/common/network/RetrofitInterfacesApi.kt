package com.example.recipeexplorerapp1.common.network

import com.example.recipeexplorerapp1.entity.RecipeListing
import com.example.recipeexplorerapp1.entity.RecipeListingData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created by firdaus on 10/19/25.
 */

interface RecipeApi {
    @Headers("Accept: application/json")
    @GET
    fun getList(
        @Url url: String,
        @Query("limit") limit: Int = 30,
        @Query("skip") skip: Int = 0
    ): Observable<RecipeListing>

    @Headers("Accept: application/json")
    @GET
    fun getRecipeById(@Url url: String): Observable<RecipeListingData>
}
