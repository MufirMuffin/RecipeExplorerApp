package com.example.recipeexplorerapp1.di.module

import android.content.Context
import android.os.Environment
import android.util.Log
//import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.example.recipeexplorerapp1.BuildConfig
import com.example.recipeexplorerapp1.utils.LocaleManager
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
class NetModule {
    @Singleton
    @Provides
    @Named("cached")
    fun provideOkHttpClient(
        @Named("logInterceptor") interceptor: HttpLoggingInterceptor,
        @Named("retryInterceptor") retryInterceptor: Interceptor,
        @Named("langInterceptor") langInterceptor: Interceptor
    ): OkHttpClient {
        val cache = Cache(Environment.getDownloadCacheDirectory(), 10 * 1024 * 1024)
        if (BuildConfig.DEBUG) return OkHttpClient.Builder()
            .addInterceptor(langInterceptor)
            .addInterceptor(interceptor)
            .addInterceptor(retryInterceptor)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .cache(cache)
            .build()

        return OkHttpClient.Builder()
            .addInterceptor(langInterceptor)
            .addInterceptor(retryInterceptor)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .cache(cache)
            .build()
    }

    @Singleton
    @Provides
    @Named("non_cached")
    fun provideNonCachedOkHttpClient(
        @Named("logInterceptor") interceptor: HttpLoggingInterceptor,
        @Named("retryInterceptor") retryInterceptor: Interceptor,
        @Named("langInterceptor") langInterceptor: Interceptor
    ): OkHttpClient {

        if (BuildConfig.DEBUG) return OkHttpClient.Builder()
            .addInterceptor(langInterceptor)
            .addInterceptor(interceptor)
            .addInterceptor(retryInterceptor)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()
        return OkHttpClient.Builder()
            .addInterceptor(langInterceptor)
            .addInterceptor(retryInterceptor)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss")
        gsonBuilder.setStrictness(Strictness.LENIENT)
        return gsonBuilder.create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, @Named("cached") client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }


    @Named("langInterceptor")
    @Singleton
    @Provides
    fun provideLanguageInterceptor(context: Context): Interceptor {
        return Interceptor {
            val request = it.request().newBuilder()
                .addHeader("Content-Language", LocaleManager.getLanguage(context)).build()
            it.proceed(request)
        }
    }


    @Named("logInterceptor")
    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
//            FirebaseCrashlytics.getInstance().log(it)
            Log.d("Network", it)
        })
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    @Singleton
    @Provides
    @Named("retryInterceptor")
    fun provideRetryInterceptor(@Named("retryCount") retryCount: Int): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            var response: Response? = null
            var exception: IOException? = null

            var tryCount = 0
            var backoff = 1000L

            while (tryCount < retryCount) {
                try {
                    response?.close()
                    response = chain.proceed(request)

                    if (response.isSuccessful || response.code in 400..499) {
                        return@Interceptor response
                    }
                } catch (e: IOException) {
                    exception = e
                }

                tryCount++

                if (tryCount < retryCount) {
                    val sleepTime = backoff + (0..250).random()
                    Thread.sleep(sleepTime)
                    backoff *= 2
                }
            }

            // throw last exception
            if (null == response && null != exception) throw exception

            // otherwise just pass the original response on
            response ?: throw IOException("Unknown network error with no response")
        }
    }

    @Singleton
    @Provides
    @Named("retryCount")
    fun provideRetryCount(): Int {
        return 3
    }
}