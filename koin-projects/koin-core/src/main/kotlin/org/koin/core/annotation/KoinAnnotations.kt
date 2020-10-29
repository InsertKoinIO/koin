package org.koin.core.annotation

@RequiresOptIn(message = "Used to extend current API with Koin API", level = RequiresOptIn.Level.ERROR)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class KoinInternal