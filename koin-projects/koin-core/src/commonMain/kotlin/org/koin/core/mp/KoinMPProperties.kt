package org.koin.core.mp

expect class KoinMPProperties {
    val size: Int
}

expect fun KoinMPProperties.toMap(): Map<String, String>
