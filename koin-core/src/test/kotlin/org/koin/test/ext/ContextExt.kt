package org.koin.test.ext

import org.koin.KoinContext
import org.koin.core.bean.BeanDefinition
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

/**
 * Context Test Utils
 */

fun KoinContext.AllDefinitions() = beanRegistry.definitions

fun KoinContext.definition(clazz: KClass<*>): BeanDefinition<*>? = AllDefinitions().keys.firstOrNull() { it.clazz == clazz }

fun KoinContext.allContext() = beanRegistry.scopes

fun KoinContext.allInstances() = instanceFactory.instances.toList()

fun KoinContext.allProperties() = propertyResolver.properties

fun KoinContext.getScope(scope: String) = beanRegistry.scopes.first { it.name == scope }

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

inline fun <reified T> KoinContext.provide(name: String = "", bind: KClass<*>? = null, scopeName: String? = null, noinline definition: () -> T) {
    val beanDefinition = BeanDefinition(name, T::class, definition = definition)
    bind?.let {
        beanDefinition.bind(bind)
    }
    val scope = if (scopeName != null) getScope(scopeName) else getScope(Scope.ROOT)
    beanRegistry.declare(beanDefinition, scope)
}