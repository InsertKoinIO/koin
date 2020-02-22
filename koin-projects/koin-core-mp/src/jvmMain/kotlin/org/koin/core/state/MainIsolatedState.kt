package org.koin.core.state

internal actual class MainIsolatedState<T:Any> actual constructor(private val startVal: T) {
    actual fun _get(): T = startVal
}