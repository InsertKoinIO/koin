package org.koin.ksp

import kotlin.reflect.KClass

/**
 * @author Fabio de Matos
 * @since 04/04/2021.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
annotation class KoinInject(
        val isSingle: Boolean = false, val bind: KClass<out Any> = Any::class
)