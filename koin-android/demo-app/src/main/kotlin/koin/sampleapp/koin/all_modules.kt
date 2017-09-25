package koin.sampleapp.koin

import koin.sampleapp.R
import koin.sampleapp.service.WeatherWS
import koin.sampleapp.weather.WeatherActivity
import koin.sampleapp.weather.WeatherContract
import koin.sampleapp.weather.WeatherPresenter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.AndroidModule
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by arnaud on 12/06/2017.
 */


fun allModules() = arrayOf(GlobalModule(), MainActivityModule())

class MainActivityModule : AndroidModule() {

    override fun context() =
            declareContext {
                // Scope WeatherActivity
                scope { WeatherActivity::class }
                provide { WeatherPresenter(get()) } bind { WeatherContract.Presenter::class }
            }
}

class GlobalModule : AndroidModule() {

    override fun context() =
            declareContext {
                // provided components
                provide { createClient() }
                provide { retrofitWS(get(), resources.getString(R.string.server_url)) }
            }

    private fun createClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .connectTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor).build()
    }

    private fun retrofitWS(okHttpClient: OkHttpClient, url: String): WeatherWS {
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        return retrofit.create(WeatherWS::class.java)
    }
}