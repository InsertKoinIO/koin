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

package org.koin.androidx.scope

import android.content.ComponentCallbacks
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.ScopeInstance

/**
 * Provide an scope for given LifecycleOwner component
 *
 * @author Arnaud Giuliani
 */

private fun LifecycleOwner.getKoin() = (this as ComponentCallbacks).getKoin()

private fun LifecycleOwner.getScopeName() = TypeQualifier(this::class)
private fun LifecycleOwner.getScopeId() = this.toString()

private fun LifecycleOwner.getOrCreateCurrentScope(): ScopeInstance {
    val scopeId = getScopeId()
    return getKoin().getScopeOrNull(scopeId) ?: createAndBindScope(scopeId, getScopeName())
}

private fun LifecycleOwner.createAndBindScope(scopeId: String, qualifier: Qualifier): ScopeInstance {
    val scope = getKoin().createScope(scopeId, qualifier)
    bindScopeToLifecycle(scope)
    return scope
}

private fun LifecycleOwner.bindScopeToLifecycle(scope: ScopeInstance) {
    lifecycle.addObserver(ScopeObserver(Lifecycle.Event.ON_DESTROY, this, scope))
}

/**
 * Get current Koin scope, bound to current lifecycle
 */
val LifecycleOwner.currentScope: ScopeInstance
    get() = getOrCreateCurrentScope()