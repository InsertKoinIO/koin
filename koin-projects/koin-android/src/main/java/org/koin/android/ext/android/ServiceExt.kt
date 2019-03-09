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
@file:Suppress("unused")

package org.koin.android.ext.android

import android.app.Service
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.ScopeInstance

/**
 * ComponentCallbacks extensions for Android
 *
 * @author Arnaud Giuliani
 */

/**
 * inject lazily given dependency for Android koincomponent
 * @param name - bean name / optional
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> Service.inject(
        name: String = "",
        scope: ScopeInstance = ScopeInstance.GLOBAL,
        noinline parameters: ParametersDefinition? = null
) = lazy { get<T>(name, scope, parameters) }

/**
 * get given dependency for Android koincomponent
 * @param name - bean name
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> Service.get(
        name: String = "",
        scope: ScopeInstance = ScopeInstance.GLOBAL,
        noinline parameters: ParametersDefinition? = null
): T = getKoin().get(name, scope, parameters)
