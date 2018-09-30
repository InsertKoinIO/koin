package fr.ekito.myweatherapp

import android.arch.persistence.room.Room
import fr.ekito.myweatherapp.data.room.WeatherDatabase
import org.koin.dsl.module.module

// Room In memroy database
val roomTestModule = module(override = true) {
    single {
        Room.inMemoryDatabaseBuilder(get(), WeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }
}