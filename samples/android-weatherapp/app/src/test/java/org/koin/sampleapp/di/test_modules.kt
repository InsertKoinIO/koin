package org.koin.sampleapp.di

import org.koin.dsl.module.applicationContext
import org.koin.sampleapp.datasource.JavaReader
import org.koin.sampleapp.repository.WeatherDatasource
import org.koin.sampleapp.repository.local.LocalDataSource
import org.koin.sampleapp.util.TestSchedulerProvider
import org.koin.sampleapp.util.rx.SchedulerProvider

val RxTestModule = applicationContext {
    // provided components
    provide { TestSchedulerProvider() } bind SchedulerProvider::class
}

val TestDataSourceModule = applicationContext {
    provide { LocalDataSource(JavaReader()) } bind WeatherDatasource::class
}

val testRemoteDatasource = listOf(RxTestModule, WeatherModule, RemoteDataSourceModule)
val testLocalDatasource = listOf(RxTestModule, WeatherModule, TestDataSourceModule)