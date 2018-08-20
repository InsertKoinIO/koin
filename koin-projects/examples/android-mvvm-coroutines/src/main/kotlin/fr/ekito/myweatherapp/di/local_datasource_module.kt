package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.data.datasource.webservice.WeatherWebDatasource
import fr.ekito.myweatherapp.data.datasource.webservice.local.AndroidJsonReader
import fr.ekito.myweatherapp.data.datasource.webservice.local.JsonReader
import fr.ekito.myweatherapp.data.datasource.webservice.local.LocalFileDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

/**
 * Local Json Files Datasource
 */
val localAndroidDatasourceModule = module {
    single<JsonReader> { AndroidJsonReader(androidContext()) }
    single<WeatherWebDatasource> { LocalFileDataSource(get(), true) }
}