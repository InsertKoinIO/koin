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
 * Resolve Koin dependency for given Type T
 *
 * <u>Note</u> this version unwrap parameters to ParametersHolder in order to let remember all parameters
 * This parameters unwrap will be triggered on recomposition
 *
 * For better performances we advise to use koinInject(Qualifier,Scope,ParametersHolder)
 *
 * @param qualifier - dependency qualifier
 * @param scope - Koin's root by default
 * @param parameters - injected parameters (with lambda & parametersOf())
 * @return instance of type T
 *
 * @author Arnaud Giuliani
 */
@Composable
@OptIn(KoinInternalApi::class)
inline fun <reified T> koinInject(
    qualifier: Qualifier? = null,
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition,
): T {
    val p = parameters.invoke()
    return remember(qualifier, scope, p) {
        scope.getWithParameters(T::class, qualifier, p)
    }
}

/**
 * Resolve Koin dependency for given Type T
 *
 * @param qualifier - dependency qualifier
 * @param scope - Koin's root by default
 * @param parametersHolder - parameters (used with parametersOf(), no lambda)
 * @return instance of type T
 *
 * @author Arnaud Giuliani
 */
@Composable
@OptIn(KoinInternalApi::class)
inline fun <reified T> koinInject(
    qualifier: Qualifier? = null,
    scope: Scope = currentKoinScope(),
    parametersHolder: ParametersHolder,
): T {
    return remember(qualifier, scope, parametersHolder) {
        scope.getWithParameters(T::class, qualifier, parametersHolder)
    }
}

/**
 * Resolve Koin dependency for given Type T
 *
 * @param qualifier - dependency qualifier
 * @param scope - Koin's root by default
 * @return instance of type T
 *
 * @author Arnaud Giuliani
 */
@Composable
inline fun <reified T> koinInject(
    qualifier: Qualifier? = null,
    scope: Scope = currentKoinScope()
): T {
    return remember(qualifier, scope) {
        scope.get(T::class, qualifier)
    }
}
