package org.koin.core.mp

import kotlinx.cinterop.get
import kotlinx.cinterop.toKString
import platform.posix.__environ
import kotlin.reflect.KClass
import kotlin.system.getTimeNanos

actual object KoinMultiPlatform {
    actual fun <K, V> emptyMutableMap(): MutableMap<K, V> {
        return HashMap()
    }

    actual fun stackTrace(throwable: Throwable): List<String> {
        return throwable.getStackTrace().toList()
    }

    actual fun nanoTime(): Long {
        return getTimeNanos()
    }

    actual fun getSystemProperties(): Map<String, String> {
        return emptyMap()
    }

    actual fun getSystemEnvironmentProperties(): Map<String, String> {
        val source = __environ ?: return emptyMap()
        val target = mutableMapOf<String, String>()
        for (i in 0..Int.MAX_VALUE) {
            val value = source.get(i) ?: break
            val str = value.toKString()
            val index = str.indexOf("=")
            if (index != -1) {
                target += str.substring(0, index) to str.substring(index + 1)
            }
        }
        return target
    }

    actual fun loadResourceString(fileName: String): String? {
        // TODO
        println("TODO: KoinMultiPlatform.loadResourceString is not yet implemented")
        return null
    }

    actual fun parseProperties(content: String): KoinMPProperties {
        // TODO
        println("TODO: KoinMultiPlatform.parseProperties is not yet implemented")
        return KoinMPProperties(emptyMap())
    }

    actual fun className(kClass: KClass<*>): String {
        return kClass.qualifiedName ?: "KClass@${hashCode()}"
    }

    actual fun printStackTrace(throwable: Throwable) {
        stackTrace(throwable).forEach(::println)
    }
}
