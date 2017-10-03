package org.koin.android

import android.app.Application
import org.koin.Koin
import org.koin.KoinContext
import org.koin.dsl.context.Scope

/**
 * init android Application - for Koin koin
 */
fun Koin.init(application: Application): Koin {
    // provide Application defintion
    beanRegistry.declare({ application }, Application::class, Scope.root())
    return this
}

/*
  val koinContext = newKoinContext(this, modules...)
 */

fun newKoinContext(application: Application, modules: List<AndroidModule>): KoinContext = Koin().init(application).build(modules)
fun newKoinContext(application: Application, vararg modules : AndroidModule): KoinContext = Koin().init(application).build(*modules)

/*
  val koinContext by lazyKoinContext(this, modules...)
 */

fun lazyKoinContext(application: Application, modules: List<AndroidModule>) = lazy { Koin().init(application).build(modules) }
fun lazyKoinContext(application: Application, vararg modules : AndroidModule) = lazy { Koin().init(application).build(*modules) }