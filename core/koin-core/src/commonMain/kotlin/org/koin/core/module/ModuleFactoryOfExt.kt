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
inline fun <reified R> Module.factoryOf(
    qualifier: Qualifier,
    crossinline constructor: () -> R,
) {
    factory(qualifier) { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R> Module.factoryOf(
    crossinline constructor: () -> R,
) {
    factory { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1> Module.factoryOf(
    qualifier: Qualifier,
    crossinline constructor: (T1) -> R,
) {
    factory(qualifier) { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1> Module.factoryOf(
    crossinline constructor: (T1) -> R,
) {
    factory { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2> Module.factoryOf(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2) -> R,
) {
    factory(qualifier) { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2> Module.factoryOf(
    crossinline constructor: (T1, T2) -> R,
) {
    factory { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3> Module.factoryOf(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3) -> R,
) {
    factory(qualifier) { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3> Module.factoryOf(
    crossinline constructor: (T1, T2, T3) -> R,
) {
    factory { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4> Module.factoryOf(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4) -> R,
) {
    factory(qualifier) { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4> Module.factoryOf(
    crossinline constructor: (T1, T2, T3, T4) -> R,
) {
    factory { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> Module.factoryOf(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
) {
    factory(qualifier) { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> Module.factoryOf(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
) {
    factory { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Module.factoryOf(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
) {
    factory(qualifier) { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Module.factoryOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
) {
    factory { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> Module.factoryOf(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
) {
    factory(qualifier) { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> Module.factoryOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
) {
    factory { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> Module.factoryOf(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
) {
    factory(qualifier) { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> Module.factoryOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
) {
    factory { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> Module.factoryOf(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
) {
    factory(qualifier) { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> Module.factoryOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
) {
    factory { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> Module.factoryOf(
    qualifier: Qualifier,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
) {
    factory(qualifier) { new(constructor) }
}

/**
 * @see factoryOf
 */
inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> Module.factoryOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
) {
    factory { new(constructor) }
}
