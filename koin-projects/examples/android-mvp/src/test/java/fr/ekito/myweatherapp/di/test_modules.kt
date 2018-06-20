package fr.ekito.myweatherapp.di

import fr.ekito.myweatherapp.data.datasource.webservice.local.JsonReader
import fr.ekito.myweatherapp.data.datasource.webservice.local.LocalFileDataSource
import fr.ekito.myweatherapp.data.datasource.webservice.WeatherWebDatasource
import fr.ekito.myweatherapp.data.datasource.webservice.local.JavaReader
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.util.TestSchedulerProvider
import org.koin.dsl.module.applicationContext


/**
 * Local java json repository
 */
val localJavaDatasourceModule = applicationContext {
    bean { JavaReader() as JsonReader }
    bean { LocalFileDataSource(get(), false) as WeatherWebDatasource }
}

/**
 * Test Rx
 */
val testRxModule = applicationContext {
    // provided components
    bean { TestSchedulerProvider() as SchedulerProvider }
}

val testWeatherApp = offlineWeatherApp + testRxModule + localJavaDatasourceModule