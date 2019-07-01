package org.koin.core.mp

expect object KoinMultiPlatform {
    fun <K, V> emptyMutableMap(): MutableMap<K, V>
}
