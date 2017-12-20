package org.koin.sampleapp.di

import org.koin.dsl.module.applicationContext
import org.koin.sampleapp.datasource.JavaReader
import org.koin.sampleapp.repository.WeatherDatasource
import org.koin.sampleapp.repository.local.LocalDataSource

val TestDataSourceModule = applicationContext {
    provide { LocalDataSource(JavaReader()) as WeatherDatasource }
}