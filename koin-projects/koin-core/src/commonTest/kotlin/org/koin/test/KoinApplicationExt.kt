package org.koin.test

import org.koin.core.KoinApplication
import org.koin.core.definition.BeanDefinition
import kotlin.reflect.KClass
import kotlin.test.assertEquals

fun KoinApplication.assertDefinitionsCount(count: Int) {
    assertEquals(count, this.koin.rootScope.beanRegistry.size(), "definitions count")
}

internal fun KoinApplication.getDefinition(clazz: KClass<*>): BeanDefinition<*>? {
    return this.koin.rootScope.beanRegistry.getDefinition(clazz)
}
