package org.koin.core.mp

actual object KoinMultiPlatform {
    actual fun <K, V> emptyMutableMap(): MutableMap<K, V> {
        return java.util.concurrent.ConcurrentHashMap()
    }
}
