package com.example.recipeexplorerapp1

import com.example.recipeexplorerapp1.base.DataBasePresenter
import com.example.recipeexplorerapp1.base.IBaseDataView
import com.example.recipeexplorerapp1.common.IRepository
import com.example.recipeexplorerapp1.common.network.CallbackWrapper
import com.example.recipeexplorerapp1.entity.RecipeListingData
import com.example.recipeexplorerapp1.utils.SchedulerProvider
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by firdaus on 10/19/25.
 */

interface IRecipeDetailsView : IBaseDataView {
    fun showRecipeDetails(recipe: RecipeListingData)
}

interface IRecipeDetailsPresenter : DataBasePresenter<IRecipeDetailsView> {
    fun loadRecipeDetails(recipeId: Int, silent: Boolean)
}

interface IRecipeDetailsModel {
    fun getRecipeById(recipeId: Int): Observable<RecipeListingData>
}

class RecipeDetailsPresenter @Inject constructor(
    override var view: IRecipeDetailsView?,
    private val model: IRecipeDetailsModel,
    override var scheduler: SchedulerProvider?
) : IRecipeDetailsPresenter {

    override var compositeDisposable = CompositeDisposable()

    override fun loadRecipeDetails(recipeId: Int, silent: Boolean) {
        if (view == null) return

        if (view?.isOnline() == false) {
            view?.showOffline { loadRecipeDetails(recipeId, false) }
            return
        }

        val subscription = model.getRecipeById(recipeId)
            .observeOn(scheduler!!.ui())
            .subscribeOn(scheduler!!.io())
            .doOnSubscribe {
                if (!silent) {
                    view?.showLoading()
                }
            }.subscribeWith(object : CallbackWrapper<RecipeListingData>(view!!,
                retry = { loadRecipeDetails(recipeId, false) }) {
                override fun onSuccess(t: RecipeListingData) {
                    view?.showRecipeDetails(t)
                }
            })
        addDisposable(subscription)
    }

    override fun attachView(view: IRecipeDetailsView, created: Boolean) {
        this.view = view
    }

    override fun deattachView() {
        view = null
    }
}

class RecipeDetailsModel @Inject constructor(
    private val repository: IRepository
) : IRecipeDetailsModel {

    override fun getRecipeById(recipeId: Int): Observable<RecipeListingData> {
        return repository.apiGetRecipeById(recipeId)
    }
}