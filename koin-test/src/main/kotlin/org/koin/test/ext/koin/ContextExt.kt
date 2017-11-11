package org.koin.test.ext.koin

import org.koin.KoinContext
import org.koin.core.bean.BeanDefinition
import org.koin.core.scope.Scope
import kotlin.reflect.KClass


/**
 * Return all definitions of Koin
 */
fun KoinContext.AllDefinitions() = beanRegistry.definitions

/**
 * return definition for given class
 * @param clazz - bean definition class
 */
fun KoinContext.definition(clazz: KClass<*>): BeanDefinition<*>? = AllDefinitions().keys.firstOrNull() { it.clazz == clazz }

/**
 * Return all contexts of Koin
 */
fun KoinContext.allContext() = beanRegistry.scopes

/**
 * Return all instances of Koin
 */
fun KoinContext.allInstances() = instanceFactory.instances.toList()

/**
 * Return all properties of Koin
 */
fun KoinContext.allProperties() = propertyResolver.properties

/**
 * return scope
 * @param scopeName - scope name
 */
fun KoinContext.getScope(scopeName: String) = beanRegistry.scopes.first { it.name == scopeName }

///**
// * Return ROOT Scope
// */
//fun KoinContext.rootScope() = beanRegistry.rootScope

/**
 * Provide a bean definition in actual KoinContext
 * @param name - bean name
 * @param bind - assignable type
 * @param scopeName - target scope name
 * @param definition - bean definition function
 */
inline fun <reified T> KoinContext.provide(name: String = "", bind: KClass<*>? = null, scopeName: String? = null, noinline definition: () -> T) {
    val beanDefinition = BeanDefinition(name, T::class, definition = definition)
    bind?.let {
        beanDefinition.bind(bind)
    }
    val scope = if (scopeName != null) getScope(scopeName) else getScope(Scope.ROOT)
    beanRegistry.declare(beanDefinition, scope)
}