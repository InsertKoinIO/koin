/*
 * Copyright 2017-2018 the original author or authors.
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
package org.koin.test

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.Scope
import org.koin.core.standalone.StandAloneKoinApplication


/**
 * Koin Test tools
 *
 * @author Arnaud Giuliani
 */

/**
 * Koin Test Component
 */
interface KoinTest {
    fun getKoin() = StandAloneKoinApplication.get().koin
}

inline fun <reified T> KoinTest.get(
    name: String? = null,
    scope: Scope? = null,
    noinline parameters: ParametersDefinition? = null
): T =
    getKoin().get(name, scope, parameters)

inline fun <reified T> KoinTest.inject(
    name: String? = null,
    scope: Scope? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> = lazy { getKoin().get<T>(name, scope, parameters) }