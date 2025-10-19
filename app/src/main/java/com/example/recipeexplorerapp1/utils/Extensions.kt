package com.example.recipeexplorerapp1.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import androidx.annotation.IdRes
import java.io.Serializable


/**
 * Created by firdaus on 10/19/25.
 */

fun <T : View> Activity.bind(@IdRes res: Int): T {
    @Suppress("UNCHECKED_CAST") return findViewById(res)
}

fun <T : View> View.bind(@IdRes res: Int): T {
    @Suppress("UNCHECKED_CAST") return findViewById(res)
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
        key, T::class.java
    )

    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}

fun Activity.isDeviceOnline(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = manager.activeNetworkInfo
    var isAvailable = false
    if (networkInfo != null && networkInfo.isConnected) {
        // Network is present and connected
        isAvailable = true
    }
    return isAvailable
}
