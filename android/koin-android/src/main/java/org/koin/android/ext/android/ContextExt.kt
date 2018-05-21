package org.koin.android.ext.android

import android.app.Application
import org.koin.dsl.context.ModuleDefinition

/**
 * Resolve Android Application instance
 */
fun ModuleDefinition.androidApplication(): Application = get()