package org.koin.mp

import kotlin.reflect.KClass

actual data class NativeClass<T : Any>(val kclass: KClass<T>)

