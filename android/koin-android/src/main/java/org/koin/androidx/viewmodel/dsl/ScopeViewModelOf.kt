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

package org.koin.androidx.viewmodel.dsl

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.module.KoinDefinition
import org.koin.core.module._factoryInstanceFactory
import org.koin.core.module.dsl.new
import org.koin.core.module.dsl.setupInstance
import org.koin.dsl.ScopeDSL

/**
 * Declare a [ScopeDSL.viewModel] definition by resolving a constructor reference for the dependency.
 * The resolution is done at compile time by leveraging inline functions, no reflection is required.
 *
 * Example:
 * ```
 * class MyViewModel : ViewModel()
 *
 * val myModule = module {
 *   viewModel(::MyViewModel)
 * }
 * ```
 *
 * @author Arnaud Giuliani
 *
 * @see new
 */
inline fun <reified R : ViewModel> ScopeDSL.viewModelOf(
    crossinline constructor: () -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel> ScopeDSL.viewModelOf(
    crossinline constructor: () -> R,
): KoinDefinition<R> = viewModel { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1> ScopeDSL.viewModelOf(
    crossinline constructor: (T1) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1> ScopeDSL.viewModelOf(
    crossinline constructor: (T1) -> R,
): KoinDefinition<R> = viewModel { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2) -> R,
): KoinDefinition<R> = viewModel { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3) -> R,
): KoinDefinition<R> = viewModel { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4) -> R,
): KoinDefinition<R> = viewModel { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
): KoinDefinition<R> = viewModel { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
): KoinDefinition<R> = viewModel { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
): KoinDefinition<R> = viewModel { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
): KoinDefinition<R> = viewModel { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
): KoinDefinition<R> = viewModel { new(constructor) }

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> = module.setupInstance(_factoryInstanceFactory(definition = { new(constructor) }, scopeQualifier = scopeQualifier), options)

/**
 * @see viewModelOf
 */
inline fun <reified R : ViewModel, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> ScopeDSL.viewModelOf(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
): KoinDefinition<R> = viewModel { new(constructor) }
