package com.example.recipeexplorerapp1.utils.timber

import android.util.Log
import timber.log.Timber


class CrashReportTree : Timber.Tree() {

    private val CRASHLYTICS_KEY_PRIORITY = "priority"
    private val CRASHLYTICS_KEY_TAG = "tag"
    private val CRASHLYTICS_KEY_MESSAGE = "message"

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if ((priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) && t == null) {
            return
        }

        t?.printStackTrace()

        if (tag != null && message != null)
            Log.d(tag, message)

//        Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority)
//        Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag)
//        Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message)

        if (t == null) {
//            Crashlytics.log(message)
        } else {
//            Crashlytics.logException(t)
        }
    }
}
