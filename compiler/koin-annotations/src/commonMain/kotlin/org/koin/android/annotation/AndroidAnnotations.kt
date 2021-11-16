/*
 * Copyright 2017-2022 the original author or authors.
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
package org.koin.android.annotation

import kotlin.reflect.KClass

/**
 * Koin definition annotation
 * Declare a type, a function as `viewModel` definition in Koin
 *
 * example:
 *
 * @KoinViewModel
 * class MyViewModel(val d : MyDependency) : ViewModel()
 *
 * will result in `viewModel { MyViewModel(get()) }`
 *
 * All dependencies are filled by constructor.
 *
 * @param binds: declared explicit types to bind to this definition. Supertypes are automatically detected
 */
@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
annotation class KoinViewModel(val binds: Array<KClass<*>> = [])
