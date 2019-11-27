package org.koin.test

import org.junit.Assert.assertEquals
import org.koin.core.KoinApplication
import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceFactory
import kotlin.reflect.KClass

fun KoinApplication.assertDefinitionsCount(count: Int) {
    assertEquals("definitions count", count, this.koin._scopeRegistry.size())
}

internal fun KoinApplication.getBeanDefinition(clazz: KClass<*>): BeanDefinition<*>? {
    return this.koin._scopeRegistry.rootScope._scopeDefinition.definitions.firstOrNull { it.primaryType == clazz }
}

internal fun KoinApplication.getInstanceFactory(clazz: KClass<*>): InstanceFactory<*>? {
    return this.koin._scopeRegistry.rootScope._instanceRegistry.instances.values.firstOrNull { it.beanDefinition.primaryType == clazz }
}