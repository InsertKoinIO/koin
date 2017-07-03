package org.koin.android

import android.app.Application
import org.koin.Koin
import org.koin.dsl.context.Scope

/**
 * init android Application - for Koin koin
 */
fun Koin.init(application: Application): Koin {
    // provide Application defintion
    beanRegistry.declare({ application }, Application::class, Scope.root())
    return this
}