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
 * TODO
 */
fun KoinComponent.bindProperty(key: String, value: Any) = (StandAloneContext.koinContext as KoinContext).propertyResolver.add(key, value)

/**
 * TODO
 */
fun KoinComponent.releaseContext(name: String) = (StandAloneContext.koinContext as KoinContext).releaseContext(name)

/**
 * TODO
 */
fun KoinComponent.releaseProperties(vararg keys: String) = (StandAloneContext.koinContext as KoinContext).releaseProperties(*keys)

