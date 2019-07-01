package org.koin.core.mp

import kotlin.reflect.KClass

expect class KoinMPClass<T : Any>

expect val <T : Any> KoinMPClass<T>.kotlin: KClass<T>
