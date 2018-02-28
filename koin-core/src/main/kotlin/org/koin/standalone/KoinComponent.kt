package org.koin.standalone

import org.koin.KoinContext
import org.koin.ParameterMap

/**
 * Koin component
 */
interface KoinComponent

/**
 * inject lazily given dependency for KoinComponent
 * @param name - bean name / optional
 */
inline fun <reified T> KoinComponent.inject(name: String = "", parameters: ParameterMap = emptyMap()) = kotlin.lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name, parameters) }

/**
 * inject lazily given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> KoinComponent.property(key: String) = kotlin.lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * inject lazily given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> KoinComponent.property(key: String, defaultValue: T) = kotlin.lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }


/**
 * Help to Access context
 */
private fun context() = (StandAloneContext.koinContext as KoinContext)

/**
 * Retrieve given dependency for KoinComponent
 * @param name - bean name / optional
 */
inline fun <reified T> KoinComponent.get(name: String = "", parameters: ParameterMap = emptyMap()) = (StandAloneContext.koinContext as KoinContext).get<T>(name, parameters)

/**
 * Retrieve given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> KoinComponent.getProperty(key: String) = (StandAloneContext.koinContext as KoinContext).getProperty<T>(key)

/**
 * Retrieve given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> KoinComponent.getProperty(key: String, defaultValue: T) = (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue)

/**
 * set a property
 * @param key
 * @param value
 */
fun KoinComponent.setProperty(key: String, value: Any) = context().setProperty(key, value)

/**
 * Release a Koin context
 * @param name
 */
fun KoinComponent.releaseContext(name: String) = context().releaseContext(name)

/**
 * Release properties
 * @param keys - key properties
 */
fun KoinComponent.releaseProperties(vararg keys: String) = context().releaseProperties(*keys)

