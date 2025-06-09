/*
 * Copyright 2017-Present the original author or authors.
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

import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID
import org.koin.ext.getFullName

/**
 * Koin Scope Component
 *
 * Help bring Scope API = Create/Destroy Scope for the given object
 *
 * @author Arnaud Giuliani
 */
interface KoinScopeComponent : KoinComponent {

    val scope: Scope
}

fun <T : Any> T.getScopeId() = this::class.getFullName() + "@" + this.hashCode()
fun <T : Any> T.getScopeName() = TypeQualifier(this::class)

fun <T : KoinScopeComponent> T.createScope(scopeId : ScopeID = getScopeId(), source: Any? = null, scopeArchetype : TypeQualifier? = null): Scope {
    return getKoin().createScope(scopeId, getScopeName(), source, scopeArchetype)
}

fun <T : KoinScopeComponent> T.createScope(source: Any? = null): Scope {
    return getKoin().createScope(getScopeId(), getScopeName(), source)
}

fun <T : KoinScopeComponent> T.getScopeOrNull(): Scope? {
    return getKoin().getScopeOrNull(getScopeId())
}

fun <T : KoinScopeComponent> T.newScope() = lazy { createScope() }
fun <T : KoinScopeComponent> T.getOrCreateScope() = lazy { getScopeOrNull() ?: createScope() }
