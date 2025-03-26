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
package org.koin.android.scope

import android.app.Service
import org.koin.android.ext.android.getKoin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.scope.Scope

fun Service.createServiceScope(): Scope {
    if (this !is AndroidScopeComponent) {
        error("Service should implement AndroidScopeComponent")
    }
    val koin = getKoin()
    val scope =
        koin.getScopeOrNull(getScopeId()) ?: koin.createScope(getScopeId(), getScopeName(), this)
    return scope
}

fun Service.destroyServiceScope() {
    if (this !is AndroidScopeComponent) {
        error("Service should implement AndroidScopeComponent")
    }
    scope.close()
}

fun Service.serviceScope() = lazy { createServiceScope() }

/**
 * Create new scope
 */
@KoinInternalApi
fun Service.createScope(source: Any? = null): Scope = getKoin().createScope(getScopeId(), getScopeName(), source)
@KoinInternalApi
fun Service.getScopeOrNull(): Scope? = getKoin().getScopeOrNull(getScopeId())