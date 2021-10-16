/*
 * Copyright 2017-2021 the original author or authors.
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
package org.koin.core.resolver

import org.koin.core.scope.Scope

/**
 * Auto resolve a dependency by using its constructor reference.
 * The resolution is done at compile time by leveraging inline functions, no reflection is required.
 *
 * Example:
 * ```
 * val myModule = module { viewModel { resolve(::MyViewModel) }
 * ```
 */
inline fun <reified R> Scope.resolve(
    constructor: () -> R,
): R = constructor()

/**
 * @see resolve
 */
inline fun <reified R, reified T1> Scope.resolve(
    constructor: (T1) -> R,
): R = constructor(get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2> Scope.resolve(
    constructor: (T1, T2) -> R,
): R = constructor(get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3> Scope.resolve(
    constructor: (T1, T2, T3) -> R,
): R = constructor(get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4> Scope.resolve(
    constructor: (T1, T2, T3, T4) -> R,
): R = constructor(get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5) -> R,
): R = constructor(get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6) -> R,
): R = constructor(get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see resolve
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20> Scope.resolve(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
