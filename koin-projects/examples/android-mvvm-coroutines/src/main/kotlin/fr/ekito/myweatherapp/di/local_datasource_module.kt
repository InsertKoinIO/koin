package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.data.datasource.webservice.WeatherWebDatasource
import fr.ekito.myweatherapp.data.datasource.webservice.local.AndroidJsonReader
import fr.ekito.myweatherapp.data.datasource.webservice.local.JsonReader
import fr.ekito.myweatherapp.data.datasource.webservice.local.LocalFileDataSource
import org.koin.dsl.module.module
import org.koin.experimental.builder.create

/**
 * Local Json Files Datasource
 */
val localAndroidDatasourceModule = module {
    single<JsonReader> { create<AndroidJsonReader>() }
    single<WeatherWebDatasource> { LocalFileDataSource(get(), true) }
}