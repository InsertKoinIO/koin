package org.koin.ext

import org.koin.core.Koin

/**
 * Koin extension functions
 *
 * Non-essential helper functions
 *
 * @author Edward Zhou
 */

/**
 * Retrieve an int property
 * @param key
 * @param defaultValue
 */
fun Koin.getIntProperty(key: String, defaultValue: Int): Int {
    return getIntProperty(key) ?: defaultValue
}

/**
 * Retrieve an int property
 * @param key
 */
fun Koin.getIntProperty(key: String): Int? {
    return getProperty(key)?.toIntOrNull()
}

/**
 * Save an int property
 * @param key
 * @param value
 */
fun Koin.setIntProperty(key: String, value: Int) {
    setProperty(key, value.toString())
}

/**
 * Retrieve a float property
 * @param key
 * @param defaultValue
 */
fun Koin.getFloatProperty(key: String, defaultValue: Float): Float {
    return getFloatProperty(key) ?: defaultValue
}

/**
 * Retrieve a float property
 * @param key
 */
fun Koin.getFloatProperty(key: String): Float? {
    return getProperty(key)?.toFloatOrNull()
}

/**
 * Save a float property
 * @param key
 * @param value
 */
fun Koin.setFloatProperty(key: String, value: Float) {
    setProperty(key, value.toString())
}
