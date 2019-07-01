package org.koin.core.mp

import kotlin.reflect.KClass

actual data class KoinMPClass<T : Any>(val kclass: KClass<T>)

actual val <T : Any> KoinMPClass<T>.kotlin: KClass<T>
    get() = kclass
