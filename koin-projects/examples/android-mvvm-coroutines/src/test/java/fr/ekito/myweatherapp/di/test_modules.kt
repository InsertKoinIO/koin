package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.data.WeatherDatasource
import fr.ekito.myweatherapp.data.local.JavaReader
import fr.ekito.myweatherapp.data.local.JsonReader
import fr.ekito.myweatherapp.data.local.LocalFileDataSource
import fr.ekito.myweatherapp.util.TestSchedulerProvider
import fr.ekito.myweatherapp.util.coroutines.SchedulerProvider
import org.koin.dsl.module.module
import org.koin.experimental.builder.singleBy


/**
 * Local java json repository
 */
val localJavaDatasourceModule = module(override = true) {
    singleBy<JsonReader, JavaReader>()
    single<WeatherDatasource> { LocalFileDataSource(get(), false) }
}

/**
 * Test Rx
 */
val testRxModule = module(override = true) {
    // provided components
    single<SchedulerProvider> { TestSchedulerProvider() }
}

val testWeatherApp = offlineWeatherApp + testRxModule + localJavaDatasourceModule