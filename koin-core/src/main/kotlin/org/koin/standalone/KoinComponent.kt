package org.koin.standalone

import org.koin.core.KoinContext
import org.koin.core.parameter.Parameters
import org.koin.dsl.context.emptyParameters

/**
 * Koin component
 */
interface KoinComponent

/**
 * inject lazily given dependency for KoinComponent
 * @param name - bean name
 */
inline fun <reified T> KoinComponent.inject(name: String = ""): Lazy<T> =
    (StandAloneContext.koinContext as KoinContext).inject(name, emptyParameters())

/**
 * inject lazily given dependency for KoinComponent
 * @param name - bean name
 * @param parameters - dynamic parameters
 */
inline fun <reified T> KoinComponent.inject(name: String = "", noinline parameters: Parameters): Lazy<T> =
    (StandAloneContext.koinContext as KoinContext).inject(name, parameters)

/**
 * inject lazily given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> KoinComponent.property(key: String): Lazy<T> =
    kotlin.lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * inject lazily given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> KoinComponent.property(key: String, defaultValue: T): Lazy<T> =
    kotlin.lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }


/**
 * Help to Access context
 */
private fun context() = (StandAloneContext.koinContext as KoinContext)

/**
 * Retrieve given dependency for KoinComponent
 * @param name - bean name
 */
inline fun <reified T> KoinComponent.get(name: String = ""): T =
    (StandAloneContext.koinContext as KoinContext).get(name, emptyParameters())

/**
 * Retrieve given dependency for KoinComponent
 * @param name - bean name
 * @param parameters - dynamic parameters
 */
inline fun <reified T> KoinComponent.get(name: String = "", noinline parameters: Parameters): T =
    (StandAloneContext.koinContext as KoinContext).get(name, parameters)

/**
 * Retrieve given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> KoinComponent.getProperty(key: String): T =
    (StandAloneContext.koinContext as KoinContext).getProperty(key)

/**
 * Retrieve given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> KoinComponent.getProperty(key: String, defaultValue: T): T =
    (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue)

/**
 * set a property
 * @param key
 * @param value
 */
fun KoinComponent.setProperty(key: String, value: Any) = context().setProperty(key, value)

/**
 * Release instances at given module scope
 * @param path
 */
fun KoinComponent.release(path: String) = context().release(path)

/**
 * Release properties
 * @param keys - key properties
 */
fun KoinComponent.releaseProperties(vararg keys: String) = context().releaseProperties(*keys)

