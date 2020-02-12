package org.koin.android.java

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.logger.Level


object KoinAndroidApplication {

    /**
     * Create Koin Application with Android context - For Java compat
     */
    @JvmStatic

    fun create(context: Context, androidLoggerLevel: Level = Level.INFO): KoinApplication {
        return KoinApplication.init().androidContext(context).androidLogger(androidLoggerLevel)
    }
}