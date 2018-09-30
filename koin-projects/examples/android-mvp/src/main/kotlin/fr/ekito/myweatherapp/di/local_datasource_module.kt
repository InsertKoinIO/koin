package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.data.WeatherDataSource
import fr.ekito.myweatherapp.data.local.AndroidJsonReader
import fr.ekito.myweatherapp.data.local.FileDataSource
import fr.ekito.myweatherapp.data.local.JsonReader
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module

/**
 * Local Json Files Datasource
 */
val localAndroidDataSourceModule = module {
    single<JsonReader> { AndroidJsonReader(androidApplication()) }
    single<WeatherDataSource> { FileDataSource(get(), true) }
}