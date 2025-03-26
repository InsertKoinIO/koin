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
package org.koin.android.ext.android

import android.content.ComponentCallbacks
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier

/**
 * Get Koin context
 */
fun ComponentCallbacks.getKoin() = when (this) {
    is KoinComponent -> this.getKoin()
    else -> GlobalContext.get()
}

/**
 * inject lazily given dependency for Android koincomponent
 * @param qualifier - bean qualifier / optional
 * @param mode - LazyThreadSafetyMode
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> ComponentCallbacks.inject(
    qualifier: Qualifier? = null,
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    noinline parameters: ParametersDefinition? = null,
) = lazy(mode) { get<T>(qualifier, parameters) }

/**
 * get given dependency for Android koincomponent
 * @param qualifier - bean qualifier / optional
 * @param parameters - injection parameters
 */
@OptIn(KoinInternalApi::class)
inline fun <reified T : Any> ComponentCallbacks.get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T {
    return getKoinScope().get(qualifier, parameters)
}
