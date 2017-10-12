package org.koin.sampleapp.di

import org.koin.android.AndroidModule
import org.koin.sampleapp.datasource.JavaReader
import org.koin.sampleapp.repository.WeatherDatasource
import org.koin.sampleapp.repository.local.LocalDataSource
import org.koin.sampleapp.rx.SchedulerProvider
import org.koin.sampleapp.util.TestSchedulerProvider

class RxTestModule : AndroidModule() {
    override fun context() = applicationContext {
        // provided components
        provide { TestSchedulerProvider() } bind (SchedulerProvider::class)
    }

    companion object {
        const val SERVER_URL = "https://my-weather-api.herokuapp.com/"
    }
}

class TestDataSourceModule : AndroidModule() {
    override fun context() = applicationContext {
        provide { LocalDataSource(JavaReader()) } bind (WeatherDatasource::class)
    }
}

fun testRemoteDatasource() = listOf(RxTestModule(), WeatherModule(), RemoteDataSourceModule())
fun testLocalDatasource() = listOf(RxTestModule(), WeatherModule(), TestDataSourceModule())