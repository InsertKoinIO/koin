package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.data.datasource.local.AndroidJsonReader
import fr.ekito.myweatherapp.data.datasource.webservice.WeatherWebDatasource
import fr.ekito.myweatherapp.data.datasource.webservice.local.JsonReader
import fr.ekito.myweatherapp.data.datasource.webservice.local.LocalFileDataSource
import org.koin.android.ext.android.androidApplication
import org.koin.dsl.module.module

/**
 * Local Json Files Datasource
 */
val localAndroidDatasourceModule = module(eager = true) {
    single { AndroidJsonReader(androidApplication()) as JsonReader }
    single { LocalFileDataSource(get(), true) as WeatherWebDatasource }
}