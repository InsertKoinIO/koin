package koin.sampleapp.di

import koin.sampleapp.service.WeatherWS
import koin.sampleapp.util.rx.ApplicationSchedulerProvider
import koin.sampleapp.util.rx.SchedulerProvider
import koin.sampleapp.weather.WeatherContract
import koin.sampleapp.weather.WeatherPresenter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.AndroidModule
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun allModules() = listOf(WeatherModule(), RxModule())

class WeatherModule : AndroidModule() {
    override fun context() = applicationContext {
        context("WeatherActivity") {
            provideFactory { WeatherPresenter(get(), get()) } bind (WeatherContract.Presenter::class)
        }

        // provided web components
        provide { createOkHttpClient() }
        // Fill property
        provide { createWebService<WeatherWS>(get(), getProperty(SERVER_URL)) }
    }

    companion object {
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

        const val SERVER_URL = "SERVER_URL"
        const val CTX_WEATHER_ACTIVITY = "WeatherActivity"
    }
}

class RxModule : AndroidModule() {
    override fun context() = applicationContext {
        provide { ApplicationSchedulerProvider() } bind (SchedulerProvider::class)
    }
}