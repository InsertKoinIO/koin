package org.koin.core.module

import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.new

/**
 * Declare a [Module.single] definition by resolving a constructor reference for the dependency.
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
inline fun <reified R> Module.single(
    qualifier: Qualifier,
    crossinline constructor: () -> R,
) {
    single(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R> Module.single(
    crossinline constructor: () -> R,
) {
    single(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1> Module.single(
    qualifier: Qualifier,
    crossinline constructor: (T1) -> R,
) {
    single(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1> Module.single(
    crossinline constructor: (T1) -> R,
) {
    single(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2> Module.single(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2) -> R,
) {
    single(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2> Module.single(
    crossinline constructor: (T1, T2) -> R,
) {
    single(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3> Module.single(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3) -> R,
) {
    single(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3> Module.single(
    crossinline constructor: (T1, T2, T3) -> R,
) {
    single(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4> Module.single(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4) -> R,
) {
    single(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4> Module.single(
    crossinline constructor: (T1, T2, T3, T4) -> R,
) {
    single(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> Module.single(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
) {
    single(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> Module.single(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
) {
    single(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Module.single(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
) {
    single(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Module.single(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
) {
    single(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> Module.single(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
) {
    single(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> Module.single(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
) {
    single(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> Module.single(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
) {
    single(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> Module.single(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
) {
    single(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> Module.single(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
) {
    single(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> Module.single(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
) {
    single(definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> Module.single(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
) {
    single(qualifier = qualifier, definition = { new(constructor) })
}

/**
 * @see factory
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> Module.single(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
) {
    single(definition = { new(constructor) })
}
