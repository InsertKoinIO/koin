package org.koin.test

import kotlin.test.assertEquals
import org.koin.core.KoinApplication
import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceFactory
import org.koin.core.scope.Scope
import org.koin.core.state.value
import kotlin.reflect.KClass

fun KoinApplication.assertDefinitionsCount(count: Int) {
    assertEquals( count, koin._koinState.value._scopeRegistry.size())
}

internal fun KoinApplication.getBeanDefinition(clazz: KClass<*>): BeanDefinition<*>? {
    return koin._koinState.value._scopeRegistry.rootScope._scopeDefinition.definitions.firstOrNull { it.primaryType == clazz }
}

internal fun Scope.getBeanDefinition(clazz: KClass<*>): BeanDefinition<*>? {
    return _scopeDefinition.definitions.firstOrNull { it.primaryType == clazz }
}

internal fun KoinApplication.getInstanceFactory(clazz: KClass<*>): InstanceFactory<*>? {
    return koin._koinState.value._scopeRegistry.rootScope.scopeState.value._instanceRegistry.instances.values.firstOrNull { it.beanDefinition.primaryType == clazz }
}

internal fun Scope.getInstanceFactory(clazz: KClass<*>): InstanceFactory<*>? {
    return scopeState.value._instanceRegistry.instances.values.firstOrNull { it.beanDefinition.primaryType == clazz }
}
