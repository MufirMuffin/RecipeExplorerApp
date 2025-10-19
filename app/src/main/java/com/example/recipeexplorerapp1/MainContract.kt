package com.example.recipeexplorerapp1

import com.example.recipeexplorerapp1.base.DataBasePresenter
import com.example.recipeexplorerapp1.base.IBaseDataView
import com.example.recipeexplorerapp1.base.IRecipeCardPresenter
import com.example.recipeexplorerapp1.base.IRecipeCardView
import com.example.recipeexplorerapp1.common.IRepository
import com.example.recipeexplorerapp1.common.network.CallbackWrapper
import com.example.recipeexplorerapp1.entity.RecipeListing
import com.example.recipeexplorerapp1.entity.RecipeListingData
import com.example.recipeexplorerapp1.utils.SchedulerProvider
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.plusAssign

/**
 * Created by firdaus on 10/19/25.
 */

interface IMainView : IBaseDataView, IRecipeCardView {
    fun showEmptyHolder()
    fun hideEmptyHolder()
    fun showLoadMoreProgress()
    fun hideLoadMoreProgress()
    fun appendRecipes(recipes: List<RecipeListingData>)

}

interface IMainPresenter : DataBasePresenter<IMainView>, IRecipeCardPresenter {
    fun loadAllRecipes(silent: Boolean)
    fun loadMoreRecipes()
    fun resetPagination()
}


interface IMainModel {
    fun getAllRecipes(limit: Int, skip: Int): Observable<RecipeListing>

}

class MainPresenter @Inject constructor(
    override var view: IMainView?,
    private val model: IMainModel,
    override var scheduler: SchedulerProvider?
) : IMainPresenter {

    private var currentSkip = 0
    private val pageLimit = 10
    private var isLoading = false
    private var hasMoreData = true
    private var totalRecipes = 0

    override var compositeDisposable = CompositeDisposable()

    override fun loadAllRecipes(silent: Boolean) {
        if (view == null) return
        if (isLoading) return

        if (view?.isOnline() == false) {
            view?.showOffline { loadAllRecipes(false) }
            return
        }

        isLoading = true
        currentSkip = 0
        hasMoreData = true

        val subscription = model.getAllRecipes(pageLimit, currentSkip)
            .observeOn(scheduler!!.ui())
            .subscribeOn(scheduler!!.io())
            .doOnSubscribe {
                if (!silent) {
                    view?.showLoading()
                    view?.hideKeyboard()
                }
            }
            .doFinally {
                isLoading = false
            }
            .subscribeWith(object : CallbackWrapper<RecipeListing>(view!!,
                retry = { loadAllRecipes(false) }) {
                override fun onSuccess(t: RecipeListing) {
                    val recipes = t.data ?: emptyList()
                    totalRecipes = t.totalSO

                    Timber.d("Loaded ${recipes.size} recipes, total: $totalRecipes")

                    view?.showRecipes(recipes)

                    if (recipes.isEmpty()) {
                        view?.showEmptyHolder()
                        hasMoreData = false
                        view?.hideLoadMoreProgress() // Hide button
                    } else {
                        view?.hideEmptyHolder()
                        currentSkip += recipes.size
                        hasMoreData = currentSkip < totalRecipes

                        // Show or hide load more button based on hasMoreData
                        if (hasMoreData) {
                            view?.hideLoadMoreProgress() // Reset button state
                        } else {
                            view?.hideLoadMoreProgress()
                            Timber.d("All recipes loaded")
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Timber.e(e, "Error loading recipes")
                    hasMoreData = false
                    view?.hideLoadMoreProgress()
                }
            })
        addDisposable(subscription)
    }

    override fun loadMoreRecipes() {
        if (view == null) return
        if (isLoading) {
            Timber.d("Already loading, skipping loadMore")
            return
        }
        if (!hasMoreData) {
            Timber.d("No more data to load")
            view?.hideLoadMoreProgress()
            return
        }

        if (view?.isOnline() == false) {
            view?.showOffline { loadMoreRecipes() }
            return
        }

        isLoading = true
        Timber.d("Loading more recipes, skip: $currentSkip")

        val subscription = model.getAllRecipes(pageLimit, currentSkip)
            .observeOn(scheduler!!.ui())
            .subscribeOn(scheduler!!.io())
            .doOnSubscribe {
                view?.showLoadMoreProgress()
            }
            .doFinally {
                isLoading = false
            }
            .subscribeWith(object : CallbackWrapper<RecipeListing>(view!!,
                retry = { loadMoreRecipes() }) {
                override fun onSuccess(t: RecipeListing) {
                    val recipes = t.data ?: emptyList()
                    totalRecipes = t.totalSO

                    Timber.d("Loaded ${recipes.size} more recipes, skip was: $currentSkip")

                    if (recipes.isNotEmpty()) {
                        view?.appendRecipes(recipes)
                        currentSkip += recipes.size
                        hasMoreData = currentSkip < totalRecipes

                        Timber.d("Has more data: $hasMoreData, currentSkip: $currentSkip, total: $totalRecipes")

                        if (hasMoreData) {
                            view?.hideLoadMoreProgress() // Keep button visible and enabled
                        } else {
                            view?.hideLoadMoreProgress() // This will hide the button
                            Timber.d("All recipes loaded, hiding load more button")
                        }
                    } else {
                        hasMoreData = false
                        view?.hideLoadMoreProgress()
                        Timber.d("No more recipes to load")
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Timber.e(e, "Error loading more recipes")
                    view?.hideLoadMoreProgress()
                    // Don't set hasMoreData to false on error, allow retry
                }
            })
        addDisposable(subscription)
    }

    override fun resetPagination() {
        currentSkip = 0
        hasMoreData = true
        isLoading = false
        totalRecipes = 0
        Timber.d("Pagination reset")
    }

    override fun attachView(view: IMainView, created: Boolean) {
        this.view = view
    }

    override fun deattachView() {
        view = null
    }

    override fun clickOnRecipe(recipe: RecipeListingData) {
        view?.showRecipe(recipe)
    }
}

class MainModel @Inject constructor(private val repository: IRepository) : IMainModel {

    override fun getAllRecipes(limit: Int, skip: Int): Observable<RecipeListing> {
        return repository.apiGetRecipes(limit, skip)
    }
}