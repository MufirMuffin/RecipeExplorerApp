package com.example.recipeexplorerapp1.common.network

//import com.google.firebase.Firebase
//import com.google.firebase.crashlytics.crashlytics
import com.example.recipeexplorerapp1.base.IBaseDataView
import io.reactivex.rxjava3.observers.DisposableObserver
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException

/**
 * Created by firdaus on 10/19/25.
 */
abstract class CallbackWrapper<T : Any>(view: IBaseDataView, var retry: (() -> Unit)? = null) :
    DisposableObserver<T>() {
    //BaseView is just a reference of a View in MVP

    private val weakReference: WeakReference<IBaseDataView>

    init {
        this.weakReference = WeakReference(view)
    }

    protected abstract fun onSuccess(t: T)

    override fun onNext(t: T) {
        //You can return StatusCodes of different cases from your API and handle it here. I usually include these cases on BaseResponse and iherit it from every Response
        onSuccess(t)
    }

    override fun onError(e: Throwable) {
        val view = weakReference.get()
        Timber.w(e)
//        Firebase.crashlytics.recordException(e)
        view?.showContent()
        when (e) {
            is HttpException -> {
                val responseCode = e.code()
                val responseBody = e.response()?.errorBody()

//                Firebase.crashlytics.log("$responseCode - ${getErrorMessage(responseBody!!)}")

                view?.onUnknownError(getErrorMessage(responseBody!!), responseCode)
            }

            is SocketTimeoutException -> {
//                Firebase.crashlytics.log("Server Timeout")
                view?.onTimeout(retry)
            }

            is IOException -> {
//                Firebase.crashlytics.log("Network Error")
                view?.onNetworkError(retry)
            }

            else -> {
                view?.onUnknownError(if (e.message != null) e.message!! else "Unknown Error")
//                Firebase.crashlytics.log(e.message ?: "Unknown Error")
            }
        }
    }

    override fun onComplete() {

    }

    protected fun getErrorMessage(responseBody: ResponseBody): String {
        return try {
            val jsonObject = JSONObject(responseBody.string())
//            Firebase.crashlytics.log(jsonObject.getString("message"))
            jsonObject.getString("message")
        } catch (e: Exception) {
//            Firebase.crashlytics.log(e.localizedMessage!!)
            e.localizedMessage!!
        }
    }
}