package koin.sampleapp.koin

import android.util.Log
import koin.sampleapp.MainActivity
import koin.sampleapp.R
import koin.sampleapp.service.WeatherService
import koin.sampleapp.service.WeatherWS
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
class MyModule : AndroidModule() {

    val TAG = MyModule::class.java.simpleName

    override fun context() =
            declareContext {
                // Scope MainActivity
                scope { MainActivity::class }
                // provided components
                provide { WeatherService(get()) }
                provide { createClient() }
                provide { retrofitWS(get(), resources.getString(R.string.server_url)) }
            }

    private fun createClient(): OkHttpClient {
        Log.i(TAG, "create OkHttpClient")
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .connectTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor).build()
    }

    private fun retrofitWS(okHttpClient: OkHttpClient, url: String): WeatherWS {
        Log.i(TAG, "create retrofitWS")
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        return retrofit.create(WeatherWS::class.java)
    }
}