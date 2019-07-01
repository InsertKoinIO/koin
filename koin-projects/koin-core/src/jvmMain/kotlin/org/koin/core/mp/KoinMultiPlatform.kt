package org.koin.core.mp

actual object KoinMultiPlatform {
    actual fun <K, V> emptyMutableMap(): MutableMap<K, V> {
        return java.util.concurrent.ConcurrentHashMap()
    }

    actual fun stackTrace(throwable: Throwable): List<String> {
        return throwable.stackTrace
            .map { it.toString() }
            .takeWhile { !it.contains("sun.reflect") }
    }
}
