package org.koin.android.ext.koin

import android.app.Application
import android.content.Context
import org.koin.Koin
import org.koin.android.module.AndroidModule
import org.koin.standalone.StandAloneContext

/**
 * init android Application - for Koin koin
 */
fun Koin.init(application: Application): Koin {
    // provide Application defintion
    provide(additionalBinding = Context::class) { application }
    return this
}

/**
 * Create a new Koin Context
 * @param application - Android application
 * @param modules - list of AndroidModule
 */
fun newKoinContext(application: Application, modules: List<AndroidModule>) {
    StandAloneContext.koinContext = Koin().init(application).build(modules)
}

/**
 * Create a new Koin Context
 * @param application - Android application
 * @param modules - vararg of AndroidModule
 */
fun newKoinContext(application: Application, vararg modules: AndroidModule) {
    StandAloneContext.koinContext = Koin().init(application).build(*modules)
}