package fr.ekito.myweatherapp.di

import android.arch.persistence.room.Room
import fr.ekito.myweatherapp.data.room.WeatherDatabase
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepositoryImpl
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepositoryRoomImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module

val roomDataSourceModule = module {

    // Weather Room Data Repository
    single<DailyForecastRepository>(override = true) { DailyForecastRepositoryRoomImpl(get(),get()) }

    // Room Database
    single {
        Room.databaseBuilder(androidApplication(), WeatherDatabase::class.java, "weather-db")
            .build()
    }

    // Expose WeatherDAO
    single { get<WeatherDatabase>().weatherDAO() }
}
