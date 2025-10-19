package com.example.recipeexplorerapp1.base

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.recipeexplorerapp1.R
import com.example.recipeexplorerapp1.databinding.BaseActivityBinding
import com.example.recipeexplorerapp1.utils.LocaleManager
import com.example.recipeexplorerapp1.utils.bind
import com.example.recipeexplorerapp1.utils.isDeviceOnline
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

abstract class BaseActivity : AppCompatActivity(), IBaseDataView {

    private lateinit var baseBinding: BaseActivityBinding

    private lateinit var smoothProgressBar: ProgressBar
    private lateinit var content: ViewGroup
    private lateinit var loadingView: ViewGroup
    private lateinit var errorView: ViewGroup
    private lateinit var errorTv: TextView
    private lateinit var dismissBtn: Button
    private lateinit var errorIcon: ImageView
    private var previousBtn: ImageButton? = null
    private var nextBtn: ImageButton? = null
    private val LANGUAGE_KEY = "language_key"
    private val LANGUAGE_COUNTRY_KEY = "language_country_key"
    var created = true

    lateinit var builder: AlertDialog.Builder
    private lateinit var dialogView: View
    private var icon: ImageView? = null
    private var titleView: TextView? = null
    private var message: TextView? = null
    private var okButton: Button? = null
    private var noButton: Button? = null

    private val activeDialogs = mutableListOf<AlertDialog>()

    abstract fun attachPresenter(recreated: Boolean)

    abstract fun deattachPresenter()

    override fun setContentView(layoutResID: Int) {
        val inflater = LayoutInflater.from(this)
        val rootView = inflater.inflate(R.layout.base_activity, null)
        inflater.inflate(layoutResID, rootView.findViewById(R.id.base_content) as ViewGroup)
        super.setContentView(rootView)
        bindViews()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseBinding = BaseActivityBinding.inflate(layoutInflater)
        created = true
    }

    private fun bindViews() {
        smoothProgressBar = bind(R.id.smooth_progressbar)
        content = bind(R.id.base_content)
        loadingView = bind(R.id.loading_view)
        errorView = bind(R.id.error_view)
        errorTv = bind(R.id.tv_error)
        dismissBtn = bind(R.id.dismiss_btn)
        errorIcon = bind(R.id.error_ic)
    }

    override fun onStart() {
        super.onStart()
        attachPresenter(created)
    }

    override fun onStop() {
        super.onStop()
        created = false
    }

    override fun onDestroy() {
        super.onDestroy()
        deattachPresenter()
        dismissAllDialogs()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        attachPresenter(false)
    }

    override fun attachBaseContext(base: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(base)
        val language = prefs.getString(LANGUAGE_KEY, "")
        val country = prefs.getString(LANGUAGE_COUNTRY_KEY, "")
        val lang = if (language == "zh_tw") {
            "zh"
        } else {
            language
        }
        if (language.isNullOrEmpty()) {
            super.attachBaseContext(LocaleManager.setLocaleDefault(base))
        } else {
            super.attachBaseContext(LocaleManager.setLocale(base, lang.toString(), country!!))
        }
    }

    protected fun dismissAllDialogs() {
        for (dialog in activeDialogs) {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
        activeDialogs.clear()
    }

    override fun showError(error: String?, handler: (() -> Unit)?) {

        showContent()
        if (!isFinishing) {
            builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            dialogView = layoutInflater.inflate(R.layout.custom_alert_dialog, null)

            icon = dialogView.findViewById(R.id.dialog_icon)
            titleView = dialogView.findViewById(R.id.dialog_title)
            message = dialogView.findViewById(R.id.dialog_message)
            okButton = dialogView.findViewById(R.id.dialog_button)

            builder.setView(dialogView)
            val alert = builder.create()

            // Keep track of this dialog
            activeDialogs.add(alert)

            titleView?.text = getString(R.string.error)
            message?.text = error
            okButton?.text = getString(R.string.dismiss)
//        button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E95D5D"))
            okButton?.setOnClickListener {
                if (handler != null) {
                    handler()
                }
                alert.dismiss()
            }
            alert.show()
        }
    }

    override fun showContent() {
        smoothProgressBar.visibility = View.GONE
        loadingView.visibility = View.GONE
        errorView.visibility = View.GONE

        content.visibility = View.VISIBLE
    }

    override fun showLoading(message: String?) {

        smoothProgressBar.visibility = View.GONE
        errorView.visibility = View.GONE

        content.visibility = View.VISIBLE
        loadingView.visibility = View.VISIBLE
    }

    override fun showSoftLoading() {
        loadingView.visibility = View.GONE
        errorView.visibility = View.GONE

        content.visibility = View.VISIBLE
        smoothProgressBar.visibility = View.VISIBLE
    }

    override fun hideSoftLoading() {
        loadingView.visibility = View.GONE
        errorView.visibility = View.GONE
        smoothProgressBar.visibility = View.GONE

        content.visibility = View.VISIBLE
    }

    override fun onUnknownError(errorMessage: String, responseCode: Int) {

        val msg: String

        when (responseCode) {
            400 -> {  //client error
                msg = getString(R.string.err400_request_error_resource_not_available)
            }
            401 -> { // Unauthorized
                msg = getString(R.string.err401_unauthorized)
            }
            403 -> {//authorization error
                msg =
                    getString(R.string.err403_your_account_does_not_exist)//"Wrong username or password, please retry."
            }
            404 -> {//url not found
                msg = getString(R.string.err404_unable_to_reach)
            }
            405 -> { // Method Not Allowed
                msg = getString(R.string.err405_method_not_allowed)
            }
            415 -> { //unsupported media type
                msg = getString(R.string.err415_unsupported_file_format)
            }
            422 -> {//Unprocessable entity
                msg = getString(R.string.err422_request_failed_response)
            }
            429 -> {// too many request
                msg = getString(R.string.err429_too_many_request)
            }
            500 -> {// server error
                msg = getString(R.string.err500_server_error)
            }
            502 -> { // Bad Gateway
                msg = getString(R.string.err502_bad_gateway)
            }
            503 -> { // Service Unavailable
                msg = getString(R.string.err503_service_unavailable)
            }
            504 -> {// timeout
                msg = getString(R.string.err504_connection_timeout)
            }
            else -> {
//                msg = "$responseCode - $errorMessage"
                msg = getString(R.string.err_general)
            }
        }

//        if (responseCode == 401 || responseCode == 403) {
//            showError(msg) {
//                Application.component?.repository()?.logoutUser()
////                LoginActivity.showLogin(this)
//            }
//            dismissBtn.setText(R.string.dismiss)
//            okButton?.setText(R.string.dismiss)
//            return
//        }
//        showError(msg)
//        dismissBtn.setText(R.string.dismiss)
//        okButton?.setText(R.string.dismiss)

        showDialogAlert(
            getString(R.string.error),
            msg,
            buttonTitle = getString(R.string.dismiss),
            handler = null
        )
    }

    override fun onNetworkError(retry: (() -> Unit)?) {
        showError(getString(R.string.network_error), retry)
        dismissBtn.setText(R.string.retry)
        okButton?.setText(R.string.retry)
    }

    override fun onTimeout(retry: (() -> Unit)?) {
        showError(getString(R.string.timeout), retry)
        dismissBtn.setText(R.string.retry)
        okButton?.setText(R.string.retry)
    }

    override fun isOnline(): Boolean {
        return isDeviceOnline()
    }

    override fun showOffline(retry: (() -> Unit)?) {
        showError(getString(R.string.no_internet_connection)) {
            showLoading()
            Single.create { emitter ->
                Thread.sleep(500)
                emitter.onSuccess(isOnline())
            }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if (result) {
                        showContent()
                        retry?.invoke()
                    } else showOffline(retry)
                }, { e -> showError(e.localizedMessage) })
        }
        if (retry != null) {
            dismissBtn.setText(R.string.retry)
            okButton?.setText(R.string.retry)
        }

    }

    override fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun showDialogAlert(
        titleAlert: String?,
        messageAlert: String?,
        imageAlert: Int?,
        buttonTitle: String?,
        handler: (() -> Unit)?
    ) {
        if (!isFinishing) {
            builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            dialogView = layoutInflater.inflate(R.layout.custom_alert_dialog, null)

            icon = dialogView.findViewById(R.id.dialog_icon)
            titleView = dialogView.findViewById(R.id.dialog_title)
            message = dialogView.findViewById(R.id.dialog_message)
            okButton = dialogView.findViewById(R.id.dialog_button)

            builder.setView(dialogView)
            val alert = builder.create()

            // Keep track of this dialog
            activeDialogs.add(alert)

            if (imageAlert != null) {
                icon?.setImageResource(imageAlert)
            }

            if (buttonTitle != null) {
                okButton?.text = buttonTitle
            }
            titleView?.text = titleAlert
            message?.text = messageAlert

//        button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E95D5D"))
            okButton?.setOnClickListener {
                if (handler != null) {
                    handler()
                }
                alert.dismiss()
            }
            alert.show()
            alert.setCancelable(false)
        }
    }

    override fun showDialogPrompt(
        titleAlert: String?,
        messageAlert: String?,
        yesButtonAlert: String?,
        noButtonAlert: String?,
        yesHandler: (() -> Unit)?,
        noHandler: (() -> Unit)?,
        yesButtonColor: String?
    ) {

        if (!isFinishing) {
            builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            dialogView = layoutInflater.inflate(R.layout.custom_prompt_dialog, null)

            titleView = dialogView.findViewById(R.id.dialog_title)
            message = dialogView.findViewById(R.id.dialog_message)
            okButton = dialogView.findViewById(R.id.positive_button)
            noButton = dialogView.findViewById(R.id.negative_button)

            builder.setView(dialogView)
            val alert = builder.create()

            // Keep track of this dialog
            activeDialogs.add(alert)

            titleView?.text = titleAlert
            message?.text = messageAlert
            okButton?.text = yesButtonAlert ?: getString(R.string.yes)
            noButton?.text = noButtonAlert ?: getString(R.string.no)

            if (titleAlert == null) {
                titleView?.visibility = View.GONE
            }

            if (yesButtonColor != null) {
                okButton?.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(yesButtonColor))
            }

            okButton?.setOnClickListener {
                if (yesHandler != null) {
                    yesHandler()
                }
                alert.dismiss()
            }
            noButton?.setOnClickListener {
                if (noHandler != null) {
                    noHandler()
                }
                alert.dismiss()
            }
            alert.show()
            alert.setCancelable(false)
        }
    }
}

