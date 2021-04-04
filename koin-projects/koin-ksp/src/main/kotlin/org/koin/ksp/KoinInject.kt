package org.koin.ksp

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

/**
 * @author Fabio de Matos
 * @since 04/04/2021.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class KoinInject()