package org.koin.android

import android.app.Application
import org.koin.Koin
import org.koin.KoinContext
import org.koin.core.scope.Scope

/**
 * init android Application - for Koin koin
 */
fun Koin.init(application: Application): Koin {
    // provide Application defintion
    provide { application }
    return this
}

/**
 * Create a new Koin Context
 * @param application - Android application
 * @param modules - list of AndroidModule
 */
fun newKoinContext(application: Application, modules: List<AndroidModule>): KoinContext = Koin().init(application).build(modules)

/**
 * Create a new Koin Context
 * @param application - Android application
 * @param modules - vararg of AndroidModule
 */
fun newKoinContext(application: Application, vararg modules: AndroidModule): KoinContext = Koin().init(application).build(*modules)

/*
  val koinContext by lazyKoinContext(this, modules...)
 */
/**
 * Create a lazy created Koin Context
 * @param application - Android application
 * @param modules - list of AndroidModule
 */
fun lazyKoinContext(application: Application, modules: List<AndroidModule>) = lazy { Koin().init(application).build(modules) }

/**
 * Create a lazy created Koin Context
 * @param application - Android application
 * @param modules - vararg of AndroidModule
 */
fun lazyKoinContext(application: Application, vararg modules: AndroidModule) = lazy { Koin().init(application).build(*modules) }