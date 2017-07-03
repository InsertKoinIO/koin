package org.koin.android

import android.app.Application
import android.content.res.AssetManager
import android.content.res.Resources
import org.koin.android.error.KoinApplicationException
import org.koin.dsl.module.Module

/**
 * Koin module with Android facilities
 */
abstract class AndroidModule : Module() {

    val applicationContext: Application by lazy { koinContet.getOrNull<Application>() ?: throw KoinApplicationException("Android application context has not been initialized. Please use init() from Koin builder.") }

    val resources: Resources by lazy { applicationContext.resources }

    val assets: AssetManager by lazy { applicationContext.assets }

}