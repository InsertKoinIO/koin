package org.koin.core.mp

import kotlin.collections.Map as KotlinMap

actual class KoinMPProperties(val map: kotlin.collections.Map<String, String>) {
    actual val size: Int
        get() = map.size
}

actual fun KoinMPProperties.toMap(): KotlinMap<String, String> {
    return map.toMap()
}
