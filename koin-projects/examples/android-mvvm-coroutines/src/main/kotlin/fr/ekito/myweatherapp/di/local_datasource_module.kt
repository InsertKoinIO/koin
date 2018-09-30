package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.data.WeatherDatasource
import fr.ekito.myweatherapp.data.local.AndroidJsonReader
import fr.ekito.myweatherapp.data.local.JsonReader
import fr.ekito.myweatherapp.data.local.LocalFileDataSource
import org.koin.dsl.module.module
import org.koin.experimental.builder.singleBy

/**
 * Local Json Files Datasource
 */
val localAndroidDatasourceModule = module {
    singleBy<JsonReader, AndroidJsonReader>()
    single<WeatherDatasource> { LocalFileDataSource(get(), true) }
}