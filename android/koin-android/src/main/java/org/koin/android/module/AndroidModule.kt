package org.koin.android.module

import android.app.Application
import android.content.Context
import org.koin.dsl.module.Module

/**
 * Koin module with Android facilities
 *
 * @author Arnaud Giuliani
 */
abstract class AndroidModule : Module() {

    /**
     * Android application component
     */
    val androidApplication: Application by lazy { koinContext.get<Application>() }

    /**
     * Android Context component
     */
    val context: Context by lazy { koinContext.get<Application>().applicationContext }
}