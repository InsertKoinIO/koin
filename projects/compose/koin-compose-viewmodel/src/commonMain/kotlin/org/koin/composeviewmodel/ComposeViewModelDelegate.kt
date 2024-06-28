package org.koin.composeviewmodel

import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.DefinitionOptions
import org.koin.core.module.dsl.new
import org.koin.core.module.dsl.onOptions
import org.koin.core.qualifier.Qualifier

inline fun <reified T : ComposeViewModel> Module.composeViewModel(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
): KoinDefinition<T> {
    return factory(qualifier, definition)
}


inline fun <reified R : ComposeViewModel> Module.composeViewModelOf(
    crossinline constructor: () -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1> Module.composeViewModelOf(
    crossinline constructor: (T1) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2> Module.composeViewModelOf(
    crossinline constructor: (T1, T2) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)

/**
 * @see ComposeViewModelOf
 */
inline fun <reified R : ComposeViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22> Module.composeViewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = composeViewModel { new(constructor) }.onOptions(options)