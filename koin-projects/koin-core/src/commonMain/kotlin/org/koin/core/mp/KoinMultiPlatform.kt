package org.koin.core.mp

import kotlin.reflect.KClass

expect object KoinMultiPlatform {
    fun <K, V> emptyMutableMap(): MutableMap<K, V>

    fun stackTrace(throwable: Throwable): List<String>

    fun nanoTime(): Long

    fun getSystemProperties(): Map<String, String>

    fun getSystemEnvironmentProperties(): Map<String, String>

    fun loadResourceString(fileName: String): String?

    fun parseProperties(content: String): KoinMPProperties

    fun className(kClass: KClass<*>): String

    fun printStackTrace(throwable: Throwable)
}
