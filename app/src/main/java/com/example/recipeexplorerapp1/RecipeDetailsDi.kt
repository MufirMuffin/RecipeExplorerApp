package com.example.recipeexplorerapp1

import com.example.recipeexplorerapp1.common.IRepository
import com.example.recipeexplorerapp1.di.component.ApplicationComponent
import com.example.recipeexplorerapp1.di.scope.ActivityScope
import com.example.recipeexplorerapp1.utils.SchedulerProvider
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * Created by firdaus on 10/19/25.
 */

@Module
class RecipeDetailsModule(private val view: IRecipeDetailsView) {

    @Provides
    @ActivityScope
    internal fun provideView(): IRecipeDetailsView {
        return view
    }

    @Provides
    @ActivityScope
    internal fun provideModel(repository: IRepository): IRecipeDetailsModel {
        return RecipeDetailsModel(repository)
    }


    @Provides
    @ActivityScope
    internal fun providePresenter(model: RecipeDetailsModel, scheduler: SchedulerProvider): IRecipeDetailsPresenter {
        return RecipeDetailsPresenter(view, model, scheduler)
    }

}


@ActivityScope
@Component(modules = arrayOf(RecipeDetailsModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface RecipeDetailsComponent {
    fun inject(activity: RecipeDetailsActivity)
}