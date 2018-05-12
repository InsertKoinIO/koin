package org.koin.android.ext.android

import android.app.Application
import org.koin.core.KoinContext
import org.koin.dsl.context.Context
import org.koin.standalone.StandAloneContext

/**
 * Resolve Android Application instance
 */
fun Context.androidApplication(): Application = get()