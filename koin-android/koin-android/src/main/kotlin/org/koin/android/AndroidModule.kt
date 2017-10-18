package org.koin.android

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
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

    /**
     * Android resources
     */
    val resources: Resources by lazy { applicationContext.resources }

    /**
     * Android assets
     */
    val assets: AssetManager by lazy { applicationContext.assets }

}