package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.data.WeatherDataSource
import fr.ekito.myweatherapp.data.local.FileDataSource
import fr.ekito.myweatherapp.data.local.JavaReader
import fr.ekito.myweatherapp.data.local.JsonReader
import fr.ekito.myweatherapp.util.TestSchedulerProvider
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import org.koin.dsl.module.module


/**
 * Local java json repository
 */
val localJavaDatasourceModule = module(override = true) {
    single<JsonReader> { JavaReader() }
    single<WeatherDataSource> { FileDataSource(get(), false) }
}

/**
 * Test Rx
 */
val testRxModule = module(override = true) {
    // provided components
    single<SchedulerProvider> { TestSchedulerProvider() }
}

val testWeatherApp = offlineWeatherApp + testRxModule + localJavaDatasourceModule