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
package org.koin.core.component

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.ext.getFullName

/**
 * Koin Scope Component
 *
 * Help bring Scope API = Create/Destroy Scope for the given object
 */
interface KoinScopeComponent : KoinComponent {

    val scope: Scope

    fun closeScope() {
        if (scope.isNotClosed()) {
            scope.close()
        }
    }
}

fun <T : Any> T.getScopeId() = this::class.getFullName() + "@" + this.hashCode()
fun <T : Any> T.getScopeName() = TypeQualifier(this::class)

fun <T : KoinScopeComponent> T.createScope(source: Any? = null): Scope {
    return getKoin().createScope(getScopeId(), getScopeName(), source)
}

fun <T : KoinScopeComponent> T.getScopeOrNull(): Scope? {
    return getKoin().getScopeOrNull(getScopeId())
}

fun <T : KoinScopeComponent> T.newScope() = lazy { createScope() }
fun <T : KoinScopeComponent> T.getOrCreateScope() = lazy { getScopeOrNull() ?: createScope() }

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