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
@file:OptIn(KoinInternalApi::class)

package org.koin.androidx.fragment.dsl

import androidx.fragment.app.Fragment
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.module.KoinDefinition
import org.koin.core.module._factoryInstanceFactory
import org.koin.core.module.dsl.new
import org.koin.core.module.dsl.setupInstance
import org.koin.dsl.ScopeDSL

/**
 * 
 */
inline fun <reified R : Fragment> ScopeDSL.fragmentOf(
    crossinline constructor: () -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment> ScopeDSL.fragmentOf(
    crossinline constructor: () -> R,
): KoinDefinition<R> = fragment { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1> ScopeDSL.fragmentOf(
    crossinline constructor: (T1) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1> ScopeDSL.fragmentOf(
    crossinline constructor: (T1) -> R,
): KoinDefinition<R> = fragment { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2) -> R,
): KoinDefinition<R> = fragment { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3) -> R,
): KoinDefinition<R> = fragment { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4) -> R,
): KoinDefinition<R> = fragment { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
): KoinDefinition<R> = fragment { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
): KoinDefinition<R> = fragment { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
): KoinDefinition<R> = fragment { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
): KoinDefinition<R> = fragment { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
): KoinDefinition<R> = fragment { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : Fragment, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> ScopeDSL.fragmentOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
): KoinDefinition<R> = fragment { new(constructor) }
