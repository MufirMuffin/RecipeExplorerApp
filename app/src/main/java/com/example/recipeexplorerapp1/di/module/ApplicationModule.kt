package com.example.recipeexplorerapp1.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.recipeexplorerapp1.Application
import com.example.recipeexplorerapp1.utils.LocaleManager
import com.example.recipeexplorerapp1.common.IRepository
import com.example.recipeexplorerapp1.common.Repository
import com.example.recipeexplorerapp1.utils.AppSchedulerProvider
import com.example.recipeexplorerapp1.utils.RxBus
import com.example.recipeexplorerapp1.utils.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.realm.Realm
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(
    includes = arrayOf(DatabaseModule::class, NetModule::class)
)


class ApplicationModule(var app: Application) {


    @Provides
    @Singleton
    fun provideApp(): Application = app

    @Provides
    @Singleton
    fun provideContext(): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(app)

    @Provides
    @Singleton
    fun provideBus(): RxBus = RxBus()

    @Provides
    @Singleton
    fun provideLocaleManager(): LocaleManager = LocaleManager

    @Provides
    @Singleton
    fun provideRepository(
        sharedPreferences: SharedPreferences, retrofit: Retrofit,
        localeManager: LocaleManager, realm: Realm
    ): IRepository = Repository(
        app.applicationContext,
        sharedPreferences,
        retrofit,
        localeManager,
        realm
    )


    @Provides
    @Singleton
    fun provideSchedulerProvider(): SchedulerProvider = AppSchedulerProvider()
}
