package org.koin.test.ext.koin

import org.koin.core.KoinContext
import org.koin.dsl.definition.BeanDefinition
import kotlin.reflect.KClass


/**
 * Return all definitions of Koin
 */
fun KoinContext.beanDefinitions() = beanRegistry.definitions

/**
 * return beanDefinition for given class
 * @param clazz - bean class
 */
fun KoinContext.beanDefinition(clazz: KClass<*>): BeanDefinition<*>? =
    beanDefinitions().firstOrNull() { it.clazz == clazz }

/**
 * Return all contexts of Koin
 */
fun KoinContext.allScopes() = pathRegistry.paths

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
fun KoinContext.getScope(scopeName: String) = allScopes().first { it.name == scopeName }
