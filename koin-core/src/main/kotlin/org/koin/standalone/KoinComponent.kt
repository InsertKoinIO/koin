package org.koin.standalone

import org.koin.KoinContext

/**
 * Koin component
 */
interface KoinComponent

/**
 * inject lazily given dependency for KoinComponent
 * @param name - bean name / optional
 */
inline fun <reified T> KoinComponent.inject(name: String = "") = kotlin.lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name) }

/**
 * lazy inject given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> KoinComponent.property(key: String) = kotlin.lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * lazy inject  given property for KoinComponent
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

