package org.koin.test.ext

import org.koin.Koin
import org.koin.KoinContext
import org.koin.core.bean.BeanDefinition
import org.koin.dsl.module.Module
import org.koin.standalone.StandAloneContext
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

//fun KoinContext.getScopeInstances(getScopeForDefinition: KClass<*>) = getScopeForDefinition(getScopeForDefinition).instanceFactory.instances

inline fun <reified T> KoinContext.getOrNull(name: String = ""): T? {
    var instance: T? = null
    try {
        instance = if (name.isNotEmpty()) {
            this.get<T>(name)
        } else {
            this.get<T>()
        }
    } catch (e: Exception) {
        resolutionStack.clear()
    }
    return instance
}

fun KoinContext.rootScope() = beanRegistry.rootScope

/**
 * Koin Context builder
 */
fun startContext(list: List<Module>) {
    StandAloneContext.koinContext = Koin().build(list)
}

/**
 * Koin Context builder
 */
fun startContext(vararg list: Module) {
    StandAloneContext.koinContext = Koin().build(*list)
}