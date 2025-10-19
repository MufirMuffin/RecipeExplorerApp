package com.example.recipeexplorerapp1.common

import com.example.recipeexplorerapp1.entity.RecipeListing
import com.example.recipeexplorerapp1.entity.RecipeListingData
import io.reactivex.rxjava3.core.Observable

/**
 * Created by firdaus on 10/19/25.
 */
interface IRepository {

    fun apiGetRecipes(limit: Int = 30, skip: Int = 0): Observable<RecipeListing>

    // Add this new method
    fun apiGetRecipeById(recipeId: Int): Observable<RecipeListingData>
}


