package org.koin.core.mp

actual object KoinMultiPlatform {
    actual fun <K, V> emptyMutableMap(): MutableMap<K, V> {
        return HashMap()
    }

    actual fun stackTrace(throwable: Throwable): List<String> {
        return throwable.getStackTrace().toList()
    }
}
