package org.koin.sampleapp.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.module.AndroidModule
import org.koin.sampleapp.repository.WeatherDatasource
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.rx.ApplicationSchedulerProvider
import org.koin.sampleapp.rx.SchedulerProvider
import org.koin.sampleapp.weather.WeatherContract
import org.koin.sampleapp.weather.WeatherPresenter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun weatherAppModules() = listOf(WeatherModule(), RemoteDataSourceModule(), RxModule())

class WeatherModule : AndroidModule() {
    override fun context() = applicationContext {
        context(name = CTX_WEATHER_ACTIVITY) {
            provideFactory { WeatherPresenter(get(), get()) } bind (WeatherContract.Presenter::class)
        }

        provide { WeatherRepository(get()) }
    }

    companion object {
        const val SERVER_URL = "SERVER_URL"
        const val CTX_WEATHER_ACTIVITY = "WeatherActivity"
    }
}

class RemoteDataSourceModule : AndroidModule() {
    override fun context() = applicationContext {
        // provided web components
        provide { createOkHttpClient() }

        // Fill property
        provide { createWebService<WeatherDatasource>(get(), getProperty(WeatherModule.SERVER_URL)) }
    }
}

fun createOkHttpClient(): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return OkHttpClient.Builder()
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor).build()
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {
    val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
    return retrofit.create(T::class.java)
}

class RxModule : AndroidModule() {
    override fun context() = applicationContext {
        provide { ApplicationSchedulerProvider() } bind (SchedulerProvider::class)
    }
}