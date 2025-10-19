package com.example.recipeexplorerapp1

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
//import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.example.recipeexplorerapp1.di.component.ApplicationComponent
import com.example.recipeexplorerapp1.di.component.DaggerApplicationComponent
import com.example.recipeexplorerapp1.di.module.ApplicationModule
import com.example.recipeexplorerapp1.utils.LocaleManager
import com.example.recipeexplorerapp1.utils.QaAppLifeCicle
import com.example.recipeexplorerapp1.utils.timber.CrashReportTree
import timber.log.Timber

class Application : Application() {

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        var component: ApplicationComponent? = null
            private set

        var activeIncidentChatId: String? = null
        var unreturnNotifyOnce: Boolean? = false
        var passwordPolicyNotifyOnce: Boolean? = false
        var broadcastSeen: Boolean? = false
        var countEnrouteAndCheckin: Int = 0
        var userPermissions: List<String>? = null

        var baseDomainName: String = ""

    }

    override fun onCreate() {
        super.onCreate()

        //di
        component =
            DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()

        //Crashlytics
//        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportTree())
        }

        registerActivityLifecycleCallbacks(QaAppLifeCicle())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocaleDefault(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.setLocaleDefault(base))
        MultiDex.install(this)
    }
}

@GlideModule
class AppGlideModule : AppGlideModule()