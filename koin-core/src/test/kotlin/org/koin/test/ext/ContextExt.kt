package org.koin.test.ext

import org.koin.KoinContext
import org.koin.core.bean.BeanDefinition
import kotlin.reflect.KClass

/**
 * Context Test Utils
 */

fun KoinContext.AllDefinitions() = beanRegistry.definitions

fun KoinContext.definition(clazz: KClass<*>): BeanDefinition<*>? = AllDefinitions().keys.firstOrNull() { it.clazz == clazz }

fun KoinContext.allContext() = beanRegistry.scopes

fun KoinContext.allInstances() = instanceFactory.instances.toList()

fun KoinContext.allProperties() = propertyResolver.registry.properties

fun KoinContext.getScope(scope: String) = beanRegistry.scopes.first { it.name == scope }

//fun KoinContext.getScopeInstances(getScope: KClass<*>) = getScope(getScope).instanceFactory.instances

inline fun <reified T> KoinContext.getOrNull(name: String = ""): T? {
    var instance: T? = null
    try {
        instance = if (name.isNotEmpty()) {
            this.get<T>(name)
        } else {
            this.get<T>()
        }
    } catch (e: Exception) {
    }
    return instance
}

fun KoinContext.rootScope() = beanRegistry.rootScope