package org.koin.core.module

import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.new

/**
 * Declare a [Module.factory] definition by resolving a constructor reference for the dependency.
 * The resolution is done at compile time by leveraging inline functions, no reflection is required.
 *
 * Example:
 * ```
 * class ViewModel
 *
 * val myModule = module {
 *   factory(::ViewModel)
 * }
 * ```
 *
 * @author Marcello Galhardo
 *
 * @see new
 */
inline fun <reified R> Module.factory(
    qualifier: Qualifier,
    crossinline constructor: () -> R,
) {
    factory(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R> Module.factory(
    crossinline constructor: () -> R,
) {
    factory(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1> Module.factory(
    qualifier: Qualifier,
    crossinline constructor: (T1) -> R,
) {
    factory(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1> Module.factory(
    crossinline constructor: (T1) -> R,
) {
    factory(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2> Module.factory(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2) -> R,
) {
    factory(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2> Module.factory(
    crossinline constructor: (T1, T2) -> R,
) {
    factory(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3> Module.factory(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3) -> R,
) {
    factory(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3> Module.factory(
    crossinline constructor: (T1, T2, T3) -> R,
) {
    factory(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4> Module.factory(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4) -> R,
) {
    factory(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4> Module.factory(
    crossinline constructor: (T1, T2, T3, T4) -> R,
) {
    factory(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> Module.factory(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
) {
    factory(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> Module.factory(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
) {
    factory(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Module.factory(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
) {
    factory(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Module.factory(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
) {
    factory(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> Module.factory(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
) {
    factory(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> Module.factory(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
) {
    factory(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> Module.factory(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
) {
    factory(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> Module.factory(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
) {
    factory(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> Module.factory(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
) {
    factory(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> Module.factory(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
) {
    factory(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> Module.factory(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
) {
    factory(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> Module.factory(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
) {
    factory(definition = { new(constructor) })
}
