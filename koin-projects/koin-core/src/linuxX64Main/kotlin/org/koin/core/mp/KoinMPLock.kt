package org.koin.core.mp

actual data class KoinMPLock<T> actual constructor(override val subject: T) :
    AbstractKoinMPLock<T>()
