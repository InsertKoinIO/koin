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
package org.koin.core.scope

import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.ext.getFullName
import org.koin.mp.PlatformTools

/**
 * Koin Scope Component
 *
 * Help bring Scope API = Create/Destroy Scope for the given object
 */
@OptIn(KoinApiExtension::class)
interface KoinScopeComponent : KoinComponent {

    val scope: Scope

    fun closeScope() {
        scope.close()
    }
}

fun <T : KoinScopeComponent> T.getScopeId() = this::class.getFullName() + "@" + this.hashCode()
fun <T : KoinScopeComponent> T.getScopeName() = TypeQualifier(this::class)
fun <T : KoinScopeComponent> T.newScope(source: Any? = null): Scope {
    return getKoin().createScope(getScopeId(), getScopeName(), source)
}

/**
 * inject lazily
 * @param qualifier - bean qualifier / optional
 * @param mode - Lazy initialization mode - see LazyThreadSafetyMode
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> KoinScopeComponent.inject(
        qualifier: Qualifier? = null,
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
        noinline parameters: ParametersDefinition? = null
) = lazy(mode) { get<T>(qualifier, parameters) }

/**
 * get given dependency
 * @param qualifier - bean name
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> KoinScopeComponent.get(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): T = scope.get(qualifier, parameters)