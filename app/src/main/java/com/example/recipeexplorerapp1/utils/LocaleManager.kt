package com.example.recipeexplorerapp1.utils

/**
 * Created by firdaus on 10/19/25.
 */

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import androidx.preference.PreferenceManager
import java.util.*


object LocaleManager {

    const val LANGUAGE_ENGLISH: String = "en"
    const val LANGUAGE_THAI: String = "th"
    const val LANGUAGE_CHINESE: String = "zh"
    const val LANGUAGE_MALAY: String = "ms"
    const val LANGUAGE_TRADITIONAL_CHINESE: String = "zh_tw"

    const val LANGUAGE_NAME_ENGLISH: String = "English"
    const val LANGUAGE_NAME_THAI: String = "ภาษาไทย"
    const val LANGUAGE_NAME_CHINESE: String = "简体中文"
    const val LANGUAGE_NAME_MALAY: String = "Bahasa Malaysia"
    const val LANGUAGE_NAME_TRADITIONAL_CHINESE: String = "繁体中文"

    const val LANGUAGE_COUNTRY_ENGLISH: String = "US"
    const val LANGUAGE_COUNTRY_THAI: String = "TH"
    const val LANGUAGE_COUNTRY_CHINESE: String = "CN"
    const val LANGUAGE_COUNTRY_MALAY: String = "MY"
    const val LANGUAGE_COUNTRY_TRADITIONAL_CHINESE: String = "TW"

    private const val LANGUAGE_KEY = "language_key"
    private const val LANGUAGE_COUNTRY_KEY = "language_country_key"

    fun setLocale(c: Context, l: String, ctry: String): Context {
//        return updateResources(c, getLanguage(c))
        return updateResources(c, l, ctry)
    }

    fun setLocaleDefault(c: Context): Context {
        return updateResources(c, getLanguage(c), getCountry(c))
    }

    fun setNewLocale(c: Context, language: String, country: String) {
        persistLanguage(c, language, country)
        restartApplication(c)
    }

    fun getLanguage(c: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        val locale = getLocale(c.resources)
        val def = when (locale.language) {
            LANGUAGE_THAI -> LANGUAGE_THAI
            LANGUAGE_MALAY -> LANGUAGE_MALAY
            LANGUAGE_CHINESE -> LANGUAGE_CHINESE
            LANGUAGE_TRADITIONAL_CHINESE -> LANGUAGE_CHINESE
            else -> LANGUAGE_ENGLISH
        }
        return prefs.getString(LANGUAGE_KEY, def).toString()
    }

    fun getCountry(c: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        val locale = getLocale(c.resources)
        val def = when (locale.language) {
            LANGUAGE_THAI -> LANGUAGE_COUNTRY_THAI
            LANGUAGE_MALAY -> LANGUAGE_COUNTRY_MALAY
            LANGUAGE_CHINESE -> LANGUAGE_COUNTRY_CHINESE
            LANGUAGE_TRADITIONAL_CHINESE -> LANGUAGE_COUNTRY_TRADITIONAL_CHINESE
            else -> LANGUAGE_COUNTRY_ENGLISH
        }

        return prefs.getString(LANGUAGE_COUNTRY_KEY, def).toString()
    }

    private fun restartApplication(context: Context) {

//        val mStartActivity = Intent(context, SplashActivity::class.java)
//        val mPendingIntentId = 123456
//        val mPendingIntent = PendingIntent.getActivity(context,
//                mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT)
//        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
//
//        exitProcess(0)
        Runtime.getRuntime().exit(0)
    }

    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(c: Context, language: String, country: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        prefs.edit().putString(LANGUAGE_KEY, language).commit()
        prefs.edit().putString(LANGUAGE_COUNTRY_KEY, country).commit()
    }

    private fun updateResources(context: Context, language: String, country: String): Context {
        var context1 = context
        val locale = Locale(language, country)
        Locale.setDefault(locale)

        val res = context1.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        context1 = context1.createConfigurationContext(config)
        return context1
    }

    fun getLocale(res: Resources): Locale {
        val config = res.configuration
        return config.locales.get(0)
    }

    fun getLanguageName(context: Context): String {
        val lang = when (getCountry(context)) {
            LANGUAGE_COUNTRY_ENGLISH -> LANGUAGE_NAME_ENGLISH
            LANGUAGE_COUNTRY_MALAY -> LANGUAGE_NAME_MALAY
            LANGUAGE_COUNTRY_CHINESE -> LANGUAGE_NAME_CHINESE
            LANGUAGE_COUNTRY_THAI -> LANGUAGE_NAME_THAI
            LANGUAGE_COUNTRY_TRADITIONAL_CHINESE -> LANGUAGE_NAME_TRADITIONAL_CHINESE
            else -> LANGUAGE_NAME_ENGLISH
        }
        return lang
    }

}