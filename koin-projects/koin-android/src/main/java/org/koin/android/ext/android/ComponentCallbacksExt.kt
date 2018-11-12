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
package org.koin.android.ext.android

import android.content.ComponentCallbacks
import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.Scope
import org.koin.core.standalone.StandAloneKoinApplication

/**
 * ComponentCallbacks extensions for Android
 *
 * @author Arnaud Giuliani
 */

/**
 * Get Koin context
 */
fun ComponentCallbacks.getKoin(): Koin = StandAloneKoinApplication.get().koin

/**
 * inject lazily given dependency for Android koincomponent
 * @param name - bean name / optional
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> ComponentCallbacks.inject(
    name: String = "",
    scope: Scope? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy { get<T>(name, scope, parameters) }

/**
 * get given dependency for Android koincomponent
 * @param name - bean name
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> ComponentCallbacks.get(
    name: String = "",
    scope: Scope? = null,
    noinline parameters: ParametersDefinition? = null
): T = getKoin().get(name, scope, parameters)
