package fr.ekito.myweatherapp.data.datasource.room

import android.arch.persistence.room.TypeConverter
import java.util.*

/**
 * Help Convert Data types for Room
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}