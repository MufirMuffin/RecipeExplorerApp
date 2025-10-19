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
class MainModule(private val view: IMainView) {

    @Provides
    @ActivityScope
    internal fun provideView(): IMainView {
        return view
    }

    @Provides
    @ActivityScope
    internal fun provideModel(repository: IRepository): IMainModel {
        return MainModel(repository)
    }


    @Provides
    @ActivityScope
    internal fun providePresenter(model: MainModel, scheduler: SchedulerProvider): IMainPresenter {
        return MainPresenter(view, model, scheduler)
    }

}


@ActivityScope
@Component(modules = arrayOf(MainModule::class), dependencies = arrayOf(ApplicationComponent::class))
interface MainComponent {
    fun inject(activity: MainActivity)
}