package org.koin.core.mp

import kotlin.collections.Map as KotlinMap

actual typealias KoinMPProperties = java.util.Properties

actual fun KoinMPProperties.toMap(): KotlinMap<String, String> {
    @Suppress("UNCHECKED_CAST")
    return (this as KotlinMap<Any?, Any?>).toMap() as KotlinMap<String, String>
}
