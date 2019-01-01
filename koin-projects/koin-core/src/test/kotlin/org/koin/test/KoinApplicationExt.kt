package org.koin.test

import org.junit.Assert.assertEquals
import org.koin.core.KoinApplication
import org.koin.core.bean.BeanDefinition
import kotlin.reflect.KClass

fun KoinApplication.assertDefinitionsCount(count: Int) {
    assertEquals("definitions count", count, this.koin.beanRegistry.size())
}

internal fun KoinApplication.getDefinition(clazz: KClass<*>): BeanDefinition<*>? {
    return this.koin.beanRegistry.getDefinition(clazz)
}