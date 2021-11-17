package org.koin.android.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
annotation class KoinViewModel(val binds: Array<KClass<*>> = [])
