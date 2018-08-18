package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.data.datasource.webservice.local.AndroidJsonReader
import fr.ekito.myweatherapp.data.datasource.webservice.WeatherWebDatasource
import fr.ekito.myweatherapp.data.datasource.webservice.local.JsonReader
import fr.ekito.myweatherapp.data.datasource.webservice.local.LocalFileDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.ext.single
import org.koin.dsl.module.module

/**
 * Local Json Files Datasource
 */
val localAndroidDatasourceModule = module(createOnStart = true) {
//    single { AndroidJsonReader(androidContext()) as JsonReader }
//    single<AndroidJsonReader>() bind JsonReader::class
    single<AndroidJsonReader,JsonReader>()

    single { LocalFileDataSource(get(), true) as WeatherWebDatasource }
}