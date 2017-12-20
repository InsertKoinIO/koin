package org.koin.android.ext.koin

import android.app.Application
import org.koin.dsl.context.Context

/**
 * Resolve Android Application instance
 */
fun Context.androidApplication() = get<Application>()
