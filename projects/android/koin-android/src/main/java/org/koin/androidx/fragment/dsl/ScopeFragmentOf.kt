/*
 * Copyright 2017-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.koin.androidx.fragment.dsl

import androidx.fragment.app.Fragment
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.dsl.*
import org.koin.dsl.ScopeDSL

/**
 * Fragment Constructor DSL
 *
 * @author Arnaud Giuliani
 * @see new
 */
inline fun <reified R : Fragment> ScopeDSL.fragmentOf(
    crossinline constructor: () -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1> ScopeDSL.fragmentOf(
    crossinline constructor: (T1) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)

/**
 * @see fragmentOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R,
    noinline options: DefinitionOptions<R>? = null,
): KoinDefinition<R> = fragment { new(constructor) }.onOptions(options)
