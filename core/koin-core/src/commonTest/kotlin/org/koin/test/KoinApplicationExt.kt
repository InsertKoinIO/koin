package org.koin.test

import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternal
import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceFactory
import org.koin.core.scope.Scope
import kotlin.reflect.KClass
import kotlin.test.assertEquals

@OptIn(KoinInternal::class)
fun KoinApplication.assertDefinitionsCount(count: Int) {
    assertEquals(count, this.koin.scopeRegistry.size(), "definitions count")
}

@OptIn(KoinInternal::class)
internal fun KoinApplication.getBeanDefinition(clazz: KClass<*>): BeanDefinition<*>? {
    return this.koin.scopeRegistry.rootScope._scopeDefinition.definitions.firstOrNull { it.primaryType == clazz }
}

@OptIn(KoinInternal::class)
internal fun Scope.getBeanDefinition(clazz: KClass<*>): BeanDefinition<*>? {
    return _scopeDefinition.definitions.firstOrNull { it.primaryType == clazz }
}

@OptIn(KoinInternal::class)
internal fun KoinApplication.getInstanceFactory(clazz: KClass<*>): InstanceFactory<*>? {
    return this.koin.scopeRegistry.rootScope.instanceRegistry.instances.values.firstOrNull { it.beanDefinition.primaryType == clazz }
}