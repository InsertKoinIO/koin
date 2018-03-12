package org.koin.ktor.ext

import io.ktor.application.Application
import org.koin.KoinContext
import org.koin.ParameterMap
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * inject lazily given dependency
 * @param name - bean name / optional
 */
inline fun <reified T> Application.inject(name: String = "", parameters: ParameterMap = emptyMap()) = lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name, parameters) }

/**
 * lazy inject given property
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Application.property(key: String) = lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * lazy inject  given property
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Application.property(key: String, defaultValue: T) = lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }

/**
 * Retrieve given dependency for KoinComponent
 * @param name - bean name / optional
 */
inline fun <reified T> Application.get(name: String = "", parameters: ParameterMap = emptyMap()) = (StandAloneContext.koinContext as KoinContext).get<T>(name, parameters)

/**
 * Retrieve given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Application.getProperty(key: String) = (StandAloneContext.koinContext as KoinContext).getProperty<T>(key)

/**
 * Retrieve given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Application.getProperty(key: String, defaultValue: T) = (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue)


/**
 * Help work on Context
 */
private fun context() = (StandAloneContext.koinContext as KoinContext)

/**
 * Set property value
 *
 * @param key - key property
 * @param value - property value
 *
 */
fun Application.setProperty(key: String, value: Any) = context().setProperty(key, value)

/**
 * Request given dependency on every call for [Application]
 * @param name - bean name / optional
 */
@Suppress("unused")
inline fun <reified T> Application.provider(
        name: String = "",
        parameters: ParameterMap = emptyMap()
): ReadOnlyProperty<Application, T> {
    return object : ReadOnlyProperty<Application, T> {

        override fun getValue(thisRef: Application, property: KProperty<*>): T {
            return (StandAloneContext.koinContext as KoinContext).get(name, parameters)
        }
    }
}
