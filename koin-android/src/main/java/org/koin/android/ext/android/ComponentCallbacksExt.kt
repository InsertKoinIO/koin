package org.koin.android.ext.android

import android.content.ComponentCallbacks
import org.koin.android.ext.koin.context
import org.koin.core.KoinContext
import org.koin.core.parameter.Parameters
import org.koin.dsl.context.emptyParameters
import org.koin.standalone.StandAloneContext

/**
 * inject lazily given dependency for Android component
 * @param name - bean name / optional
 */
inline fun <reified T> ComponentCallbacks.inject(
    name: String = ""
): Lazy<T> = inject(name, emptyParameters())

/**
 * inject lazily given dependency for Android component
 * @param name - bean name / optional
 * @param parameters - dynamic parameters
 */
inline fun <reified T> ComponentCallbacks.inject(
    name: String = "",
    noinline parameters: Parameters
): Lazy<T> = (StandAloneContext.koinContext as KoinContext).inject(name, parameters)

/**
 * get given dependency for Android component
 * @param name - bean name / optional
 */
inline fun <reified T> ComponentCallbacks.get(
    name: String = ""
): T = get(name, emptyParameters())

/**
 * get given dependency for Android component
 * @param name - bean name
 * @param parameters - dynamic parameters
 */
inline fun <reified T> ComponentCallbacks.get(
    name: String = "",
    noinline parameters: Parameters
): T = (StandAloneContext.koinContext as KoinContext).get(name, parameters)

/**
 * lazy inject given property for Android component
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> ComponentCallbacks.property(key: String): Lazy<T> =
    lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * lazy inject  given property for Android component
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> ComponentCallbacks.property(key: String, defaultValue: T): Lazy<T> =
    lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }

/**
 * Set a property
 *
 * @param key
 * @param value
 */
fun ComponentCallbacks.setProperty(key: String, value: Any): Unit =
    context().setProperty(key, value)

/**
 * Release properties
 * @param keys - property keys
 */
fun ComponentCallbacks.releaseProperties(vararg keys: String): Unit =
    context().releaseProperties(*keys)

/**
 * Release a context
 * @param name
 */
fun ComponentCallbacks.releaseContext(name: String): Unit = context().release(name)
