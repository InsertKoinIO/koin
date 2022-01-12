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
package org.koin.core.scope

/**
 * Auto resolve a class dependencies by using its constructor reference.
 * The resolution is done at compile time by leveraging inline functions, no reflection is required.
 *
 * Example:
 * ```
 * val myModule = module {
 *   viewModel { new(::MyViewModel) }
 * }
 * ```
 *
 * @author Marcello Galhardo
 */
inline fun <reified R> Scope.new(
    constructor: () -> R,
): R = constructor()

/**
 * @see new
 */
inline fun <reified R, reified T1> Scope.new(
    constructor: (T1) -> R,
): R = constructor(get())

/**
 * @see new
 */
inline fun <reified R, reified T1, reified T2> Scope.new(
    constructor: (T1, T2) -> R,
): R = constructor(get(), get())

/**
 * @see new
 */
inline fun <reified R, reified T1, reified T2, reified T3> Scope.new(
    constructor: (T1, T2, T3) -> R,
): R = constructor(get(), get(), get())

/**
 * @see new
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4> Scope.new(
    constructor: (T1, T2, T3, T4) -> R,
): R = constructor(get(), get(), get(), get())

/**
 * @see new
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> Scope.new(
    constructor: (T1, T2, T3, T4, T5) -> R,
): R = constructor(get(), get(), get(), get(), get())

/**
 * @see new
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6) -> R,
): R = constructor(get(), get(), get(), get(), get(), get())

/**
 * @see new
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get())

/**
 * @see new
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see new
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get())

/**
 * @see new
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> Scope.new(
    constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
): R = constructor(get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
