package org.koin.android.ext.koin

import android.app.Application
import org.koin.Koin
import org.koin.core.bean.BeanDefinition
import org.koin.core.scope.Scope

/**
 * init android Application dependency in Koin context
 * @param application - Android Application instance
 */
fun Koin.init(application: Application): Koin {
    // provide Application defintion
    beanRegistry.declare(BeanDefinition(clazz = Application::class, definition = { application }), Scope.root())
    return this
}