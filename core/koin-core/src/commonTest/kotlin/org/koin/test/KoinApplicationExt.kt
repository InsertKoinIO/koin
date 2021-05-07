package org.koin.test

import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceFactory
import org.koin.core.scope.Scope
import kotlin.reflect.KClass
import kotlin.test.assertEquals

@OptIn(KoinInternalApi::class)
fun KoinApplication.assertDefinitionsCount(count: Int) {
    assertEquals(count, this.koin.instanceRegistry.size(), "definitions count")
}

@OptIn(KoinInternalApi::class)
internal fun KoinApplication.getBeanDefinition(clazz: KClass<*>): BeanDefinition<*>? {
    return this.koin.scopeRegistry.rootScope.getBeanDefinition(clazz)
}

@OptIn(KoinInternalApi::class)
internal fun Scope.getBeanDefinition(clazz: KClass<*>): BeanDefinition<*>? {
    return _koin.instanceRegistry.instances.values.firstOrNull { it.beanDefinition.primaryType == clazz }?.beanDefinition
}

@OptIn(KoinInternalApi::class)
internal fun KoinApplication.getInstanceFactory(clazz: KClass<*>): InstanceFactory<*>? {
    return this.koin.instanceRegistry.instances.values.firstOrNull { it.beanDefinition.primaryType == clazz }
}