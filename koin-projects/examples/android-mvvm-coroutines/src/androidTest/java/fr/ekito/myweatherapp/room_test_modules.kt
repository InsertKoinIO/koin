package fr.ekito.myweatherapp

import android.arch.persistence.room.Room
import fr.ekito.myweatherapp.data.datasource.room.WeatherDatabase
import org.koin.dsl.module.module

/**
 * In-Memory Room Database definition
 */
val roomTestModule = module(override = true) {
    single {
        Room.inMemoryDatabaseBuilder(get(), WeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }
}