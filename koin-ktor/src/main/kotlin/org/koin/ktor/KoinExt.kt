package org.koin.ktor

import io.ktor.application.Application
import org.koin.Koin
import org.koin.core.bean.BeanDefinition
import org.koin.core.scope.Scope

fun Koin.bindApplication(application: Application): Koin {
    beanRegistry.declare(BeanDefinition(clazz = Application::class, definition = { application }), beanRegistry.getScope(Scope.ROOT))
    return this
}