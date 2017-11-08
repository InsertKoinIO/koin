package org.koin.test.ext

import org.koin.KoinContext
import org.koin.core.bean.BeanDefinition
import org.koin.standalone.KoinComponent
import kotlin.reflect.KClass

/**
 * Context Test Utils
 */

fun KoinContext.AllDefinitions() = beanRegistry.definitions

fun KoinContext.definition(clazz: KClass<*>): BeanDefinition<*>? = AllDefinitions().keys.firstOrNull() { it.clazz == clazz }

fun KoinContext.allContext() = beanRegistry.scopes

fun KoinContext.allInstances() = instanceFactory.instances.toList()

fun KoinContext.allProperties() = propertyResolver.properties

fun KoinContext.getScope(name: String) = beanRegistry.getScope(name)

fun KoinContext.rootScope() = beanRegistry.rootScope

inline fun <reified T> KoinContext.getOrNull(name: String = ""): T? {
    var instance: T? = null
    try {
        instance = get(name)
    } catch (e: Exception) {
    }
    return instance
}

