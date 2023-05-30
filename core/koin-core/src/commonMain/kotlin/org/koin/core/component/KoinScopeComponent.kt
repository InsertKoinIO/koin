/*
 * Copyright 2017-2023 the original author or authors.
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

    @Deprecated("not used internaly anymore")
    fun closeScope() {
        if (scope.isNotClosed()) {
            scope.close()
        }
    }
}

inline fun <reified T : Any> T.getScopeId() = T::class.getFullName() + "@" + this.hashCode()
inline fun <reified T : Any>T.getScopeName() = TypeQualifier(T::class)

inline fun <reified T : KoinScopeComponent> T.createScope(source: Any? = null): Scope {
    return getKoin().createScope(getScopeId(), getScopeName(), source)
}

inline fun <reified T : KoinScopeComponent> T.getScopeOrNull(): Scope? {
    return getKoin().getScopeOrNull(getScopeId())
}

inline fun <reified T: KoinScopeComponent> T.newScope() = lazy { createScope() }
inline fun <reified T : KoinScopeComponent> T.getOrCreateScope() = lazy { getScopeOrNull() ?: createScope() }