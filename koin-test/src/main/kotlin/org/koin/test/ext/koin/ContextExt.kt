package org.koin.test.ext.koin

import org.koin.KoinContext
import org.koin.core.bean.BeanDefinition
import kotlin.reflect.KClass


/**
 * Return all definitions of Koin
 */
fun KoinContext.AllDefinitions() = beanRegistry.definitions

/**
 * return definition for given class
 * @param clazz - bean definition class
 */
fun KoinContext.definition(clazz: KClass<*>): BeanDefinition<*>? = AllDefinitions().firstOrNull() { it.clazz == clazz }

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
