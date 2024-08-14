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
package org.koin.androidx.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.compose.currentKoinScope
import org.koin.compose.rememberCurrentKoinScope
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

/**
 * Resolve a dependency for [Composable] functions
 * @param qualifier
 * @param parameters
 *
 * @return Lazy instance of type T
 *
 * @author Arnaud Giuliani
 * @author Henrique Horbovyi
 */
@OptIn(KoinInternalApi::class)
@Composable
@Deprecated("use koinInject() instead")
inline fun <reified T> get(
    qualifier: Qualifier? = null,
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null,
): T = remember(qualifier, parameters, scope) {
    scope.get(qualifier, parameters)
}

@Composable
@Deprecated("use org.koin.compose.getKoin() instead")
fun getKoin(): Koin = remember {
    GlobalContext.get()
}
