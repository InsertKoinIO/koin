package fr.ekito.myweatherapp.data.datasource.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Single
import java.util.*

@Dao
interface WeatherDAO {

    /**
     * Save entities
     */
    @Insert
    fun saveAll(entities: List<WeatherEntity>)

    /**
     * Find any WeatherEntity by location and date
     * @return List<WeatherEntity>
     */
    @Query("SELECT * FROM weather WHERE location = :location AND date = :date")
    fun findAllBy(location: String, date: Date): Single<List<WeatherEntity>>

    /**
     * Find Latest WeatherEntity by Date
     * @return List<WeatherEntity>
     */
    @Query("SELECT * FROM weather GROUP BY date ORDER BY date DESC LIMIT 1")
    fun findLatestWeather(): Single<List<WeatherEntity>>

    /**
     * Find WeatherEntity for given id
     * @return WeatherEntity
     */
    @Query("SELECT * FROM weather WHERE id = :id")
    fun findWeatherById(id: String): Single<WeatherEntity>
}