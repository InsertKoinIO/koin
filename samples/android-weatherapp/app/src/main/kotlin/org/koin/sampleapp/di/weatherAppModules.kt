package org.koin.sampleapp.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.module.AndroidModule
import org.koin.sampleapp.di.Properties.SERVER_URL
import org.koin.sampleapp.repository.WeatherDatasource
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.repository.WeatherRepositoryImpl
import org.koin.sampleapp.util.rx.ApplicationSchedulerProvider
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.view.main.MainContract
import org.koin.sampleapp.view.main.MainPresenter
import org.koin.sampleapp.view.weather.WeatherResultContract
import org.koin.sampleapp.view.weather.WeatherResultPresenter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Gather all app modules
fun weatherAppModules() = listOf(WeatherModule(), RemoteDataSourceModule(), RxModule())

class WeatherModule : AndroidModule() {
    override fun context() = applicationContext {
        context(CTX_MAIN_ACTIVITY) {
            provide { MainPresenter(get(), get()) } bind MainContract.Presenter::class
        }
        context(CTX_WEATHER_ACTIVITY) {
            provide { WeatherResultPresenter(get(), get()) } bind WeatherResultContract.Presenter::class
        }

        provide { WeatherRepositoryImpl(get()) } bind WeatherRepository::class
    }

    companion object {
        const val CTX_MAIN_ACTIVITY = "MainActivity"
        const val CTX_WEATHER_ACTIVITY = "WeatherResultActivity"
    }
}

object Properties {
    const val SERVER_URL = "SERVER_URL"
}

class RemoteDataSourceModule : AndroidModule() {
    override fun context() = applicationContext {
        // provided web components
        provide { createOkHttpClient() }

        // Fill property
        provide { createWebService<WeatherDatasource>(get(), getProperty(SERVER_URL)) }
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