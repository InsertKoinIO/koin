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

package org.koin.android.scope

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.ComponentCallbacks
import org.koin.android.ext.android.getKoin
import org.koin.core.scope.ScopeInstance
import org.koin.ext.getFullName


private fun LifecycleOwner.getKoin() = (this as ComponentCallbacks).getKoin()
private fun LifecycleOwner.getScopeName() = this::class.getFullName()
private fun LifecycleOwner.getScopeId() = this.toString()

val LifecycleOwner.currentScope: ScopeInstance
    get() = getOrCreateCurrentScope()

private fun LifecycleOwner.getOrCreateCurrentScope(): ScopeInstance {
    val scopeId = getScopeId()
    return getKoin().getScopeOrNull(scopeId) ?: createAndBindScope(scopeId, getScopeName())
}

private fun LifecycleOwner.createAndBindScope(scopeId: String, name: String): ScopeInstance {
    val scope = getKoin().createScope(scopeId, name)
    bindScopeToLifecycle(scope)
    return scope
}

private fun LifecycleOwner.bindScopeToLifecycle(scope: ScopeInstance) {
    lifecycle.addObserver(ScopeObserver(Lifecycle.Event.ON_DESTROY, this, scope))
}

