package com.example.recipeexplorerapp1.common

import android.content.Context
import android.content.SharedPreferences
import com.example.recipeexplorerapp1.common.network.RecipeApi
import com.example.recipeexplorerapp1.entity.RecipeListing
import com.example.recipeexplorerapp1.entity.RecipeListingData
import io.reactivex.rxjava3.core.Observable
import io.realm.Realm
import retrofit2.Retrofit


/**
 * Created by firdaus on 19/19/25.
 */

class Repository(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val retrofit: Retrofit,
    private val localeManager: com.example.recipeexplorerapp1.utils.LocaleManager,
    private val realm: Realm
) : IRepository {
    //API

    override fun apiGetRecipes(limit: Int, skip: Int): Observable<RecipeListing> {
        return retrofit.create(RecipeApi::class.java)
            .getList("https://dummyjson.com/recipes", limit, skip)
    }

    override fun apiGetRecipeById(recipeId: Int): Observable<RecipeListingData> {
        return retrofit.create(RecipeApi::class.java)
            .getRecipeById("https://dummyjson.com/recipes/$recipeId")
    }
}