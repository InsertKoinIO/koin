package org.koin.compiler.hints

/**
 * Marker annotation for generated hint functions.
 * Applied to functions in org.koin.compiler.hints package that encode @Configuration module information.
 *
 * These hint functions allow cross-module discovery of @Configuration modules from JARs.
 * The FIR plugin generates these hints, and the predicate-based provider can discover them.
 *
 * @param moduleClass The fully qualified name of the module class this hint represents
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
public annotation class KoinConfigurationHint(val moduleClass: String)
