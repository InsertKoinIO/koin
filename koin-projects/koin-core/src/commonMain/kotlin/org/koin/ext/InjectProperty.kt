package org.koin.ext

import org.koin.core.Koin
import org.koin.core.context.KoinContextHandler
import org.koin.core.scope.Scope
import kotlin.reflect.KMutableProperty0

inline fun <reified T> KMutableProperty0<T>.inject() {
    set(KoinContextHandler.get().get())
}

inline fun <reified T> KMutableProperty0<T>.inject(koin: Koin) {
    set(koin.get())
}

inline fun <reified T> KMutableProperty0<T>.inject(scope: Scope) {
    set(scope.get())
}