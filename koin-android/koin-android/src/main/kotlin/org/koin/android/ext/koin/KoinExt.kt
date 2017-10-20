package org.koin.android.ext.koin

import android.app.Application
import android.content.Context
import org.koin.Koin
import org.koin.KoinContext
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
 * Bind an Android String to Koin property
 */
fun KoinContext.bindString(id: Int, key: String) {
    setProperty(key, get<Application>().getString(id))
}

/**
 * Bind an Android Integer to Koin property
 */
fun KoinContext.bindInt(id: Int, key: String) {
    setProperty(key, get<Application>().resources.getInteger(id))
}

/**
 * Bind an Android Boolean to Koin property
 */
fun KoinContext.bindBool(id: Int, key: String) {
    setProperty(key, get<Application>().resources.getBoolean(id))
}

/**
 * Create a new Koin Context
 * @param application - Android application
 * @param modules - list of AndroidModule
 */
fun newContext(application: Application, modules: List<AndroidModule>) {
    StandAloneContext.koinContext = Koin().init(application).build(modules)
}

/**
 * Create a new Koin Context
 * @param application - Android application
 * @param modules - vararg of AndroidModule
 */
fun newContext(application: Application, vararg modules: AndroidModule) {
    StandAloneContext.koinContext = Koin().init(application).build(*modules)
}