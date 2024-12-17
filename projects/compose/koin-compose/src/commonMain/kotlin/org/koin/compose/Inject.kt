/*
 * Copyright 2017-present the original author or authors.
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
package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

/**
 * Resolve Koin dependency
 *
 * @param qualifier
 * @param scope - Koin's root default
 * @param parameters - injected parameters
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified T> koinInject(
    qualifier: Qualifier? = null,
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null,
): T {
    val params: ParametersHolder? = parameters?.invoke()
    return remember(qualifier, scope, params) {
        scope.getWithParameters(T::class, qualifier,params)
    }
}
