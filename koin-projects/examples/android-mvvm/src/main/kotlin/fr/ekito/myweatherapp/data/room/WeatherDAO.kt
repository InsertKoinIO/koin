package fr.ekito.myweatherapp.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Single
import java.util.*

@Dao
interface WeatherDAO {

    @Insert
    fun saveAll(entities: List<WeatherEntity>)

    @Query("SELECT * FROM weather WHERE id = :id")
    fun findWeatherById(id: String): Single<WeatherEntity>

    @Query("SELECT * FROM weather WHERE location = :location AND date = :date")
    fun findAllBy(location: String, date: Date): Single<List<WeatherEntity>>

    @Query("SELECT * FROM weather ORDER BY date DESC")
    fun findLatestWeather(): Single<List<WeatherEntity>>
}