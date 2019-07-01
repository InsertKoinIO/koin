package org.koin.core.mp

actual data class KoinMPLock<T> actual constructor(override val subject: T) :
    AbstractKoinMPLock<T>() {

    private val lock = Object()

    override operator fun <R> invoke(handler: T.() -> R): R {
        return synchronized(lock) {
            subject.handler()
        }
    }
}
