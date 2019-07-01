package org.koin.core.mp

import kotlin.reflect.KClass
import kotlin.jvm.kotlin as javaClassToKotlinClass

actual typealias KoinMPClass<T> = Class<T>

actual val <T : Any> KoinMPClass<T>.kotlin: KClass<T>
    get() = javaClassToKotlinClass
