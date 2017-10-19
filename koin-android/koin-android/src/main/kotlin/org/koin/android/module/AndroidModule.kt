package org.koin.android.module

import android.app.Application
import android.content.Context
import org.koin.dsl.module.Module

/**
 * Koin module with Android facilities
 */
abstract class AndroidModule : Module() {

    /**
     * Android application context
     */
    val applicationContext: Application by lazy { koinContext.get<Application>() }

    /**
     * Android context
     */
    val context: Context by lazy { koinContext.get<Context>() }
}