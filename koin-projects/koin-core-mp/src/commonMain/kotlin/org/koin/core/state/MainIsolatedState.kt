package org.koin.core.state

internal expect class MainIsolatedState<T:Any>(startVal: T) {
    fun _get(): T
}

internal inline val <T:Any> MainIsolatedState<T>.value: T
    get() {
        return _get()
    }