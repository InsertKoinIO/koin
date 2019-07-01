package org.koin.core.mp

import org.koin.core.Koin
import kotlin.reflect.KClass

actual object KoinMultiPlatform {
    actual fun <K, V> emptyMutableMap(): MutableMap<K, V> {
        return java.util.concurrent.ConcurrentHashMap()
    }

    actual fun stackTrace(throwable: Throwable): List<String> {
        return throwable.stackTrace
            .map { it.toString() }
            .takeWhile { !it.contains("sun.reflect") }
    }

    actual fun nanoTime(): Long {
        return System.nanoTime()
    }

    actual fun getSystemProperties(): Map<String, String> {
        @Suppress("UNCHECKED_CAST")
        return System.getProperties().toMap()
    }

    actual fun getSystemEnvironmentProperties(): Map<String, String> {
        @Suppress("UNCHECKED_CAST")
        return System.getenv().toProperties().toMap()
    }

    actual fun loadResourceString(fileName: String): String? {
        return Koin::class.java.getResource(fileName)?.readText()
    }

    actual fun parseProperties(content: String): KoinMPProperties {
        val properties = KoinMPProperties()
        properties.load(content.byteInputStream())
        return properties
    }

    actual fun className(kClass: KClass<*>): String {
        return kClass.java.name
    }
}
