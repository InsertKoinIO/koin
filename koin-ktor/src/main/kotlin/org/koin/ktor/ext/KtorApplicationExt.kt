package org.koin.ktor.ext

import io.ktor.application.Application
import org.koin.core.KoinContext
import org.koin.core.parameter.Parameters
import org.koin.dsl.context.emptyParameters
import org.koin.standalone.StandAloneContext

/**
 * inject lazily given dependency
 * @param name - bean name / optional
 * @param module - module path
 */
inline fun <reified T> Application.inject(
    name: String = "",
    module: String? = null
) =
    lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name, module, emptyParameters()) }

/**
 * inject lazily given dependency
 * @param name - bean name / optional
 * @param module - module path
 * @param parameters
 */
inline fun <reified T> Application.inject(
    name: String = "",
    module: String? = null,
    noinline parameters: Parameters
) =
    lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name, module, parameters) }

/**
 * Retrieve given dependency for KoinComponent
 * @param name - bean name / optional
 * @param module - module path
 */
inline fun <reified T> Application.get(
    name: String = "",
    module: String? = null
) =
    (StandAloneContext.koinContext as KoinContext).get<T>(name, module, emptyParameters())

/**
 * Retrieve given dependency for KoinComponent
 * @param name - bean name / optional
 * @param module - module path
 * @param parameters
 */
inline fun <reified T> Application.get(
    name: String = "",
    module: String? = null,
    noinline parameters: Parameters
) =
    (StandAloneContext.koinContext as KoinContext).get<T>(name, module, parameters)

/**
 * lazy inject given property
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Application.property(key: String) =
    lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * lazy inject  given property
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Application.property(key: String, defaultValue: T) =
    lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }

/**
 * Retrieve given property for KoinComponent
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Application.getProperty(key: String) =
    (StandAloneContext.koinContext as KoinContext).getProperty<T>(key)

/**
 * Retrieve given property for KoinComponent
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Application.getProperty(key: String, defaultValue: T) =
    (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue)


/**
 * Help work on ModuleDefinition
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