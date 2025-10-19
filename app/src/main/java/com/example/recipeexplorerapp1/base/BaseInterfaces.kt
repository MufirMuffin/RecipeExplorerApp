package com.example.recipeexplorerapp1.base

import com.example.recipeexplorerapp1.entity.*
import com.example.recipeexplorerapp1.utils.SchedulerProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

interface IBaseView {

    fun showError(error: String? = null, handler: (() -> Unit)? = null)

    fun showContent()

    fun showLoading(message: String? = null)

    fun showSoftLoading()

    fun hideSoftLoading()

    fun hideKeyboard()
}

interface IBaseDataView : IBaseView {
    fun onUnknownError(errorMessage: String, responseCode: Int = 0)
    fun onNetworkError(retry: (() -> Unit)?)
    fun onTimeout(retry: (() -> Unit)?)
    fun isOnline(): Boolean
    fun showOffline(retry: (() -> Unit)?)

    fun showDialogAlert(
        titleAlert: String? = null,
        messageAlert: String? = null,
        imageAlert: Int? = null,
        buttonTitle: String? = null,
        handler: (() -> Unit)? = null
    )

    fun showDialogPrompt(
        titleAlert: String? = null,
        messageAlert: String? = null,
        yesButtonAlert: String? = null,
        noButtonAlert: String? = null,
        yesHandler: (() -> Unit)? = null,
        noHandler: (() -> Unit)? = null,
        yesButtonColor: String? = null
    )

}


interface IRecipeCardPresenter {
    fun clickOnRecipe(recipe: RecipeListingData)

}

interface IRecipeCardView {

    fun showRecipe(model: RecipeListingData)

    fun showRecipes(recipes: List<RecipeListingData>)

}

interface BasePresenter<T> {
    var view: T?
    var scheduler: SchedulerProvider?

    fun attachView(view: T, created: Boolean) {
        this.view = view
    }

    fun deattachView() {
        this.view = null
    }
}

interface DataBasePresenter<T> : BasePresenter<T> {

    var compositeDisposable: CompositeDisposable

    fun getDisposable(): CompositeDisposable? {

        return compositeDisposable
    }

    fun addDisposable(subscription: Disposable) {
        getDisposable()?.add(subscription)
    }

    override fun deattachView() {
        super.deattachView()
        getDisposable()?.clear()
    }

}
