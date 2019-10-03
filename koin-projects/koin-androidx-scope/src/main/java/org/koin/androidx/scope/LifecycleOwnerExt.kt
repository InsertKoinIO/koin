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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.koin.android.ext.android.getKoin
import org.koin.core.error.NoScopeDefinitionFoundException
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.*

/**
 * Provide a scope for given LifecycleOwner component
 *
 * @author Arnaud Giuliani
 */

private fun LifecycleOwner.getKoin() = (this as ComponentCallbacks).getKoin()

private fun LifecycleOwner.getOrCreateCurrentScope(): Scope {
    val scopeId = getScopeId()
    return getKoin().getScopeOrNull(scopeId) ?: createAndBindScope(scopeId, getScopeName(), parentScopeId)
}

private fun LifecycleOwner.createAndBindScope(scopeId: String, qualifier: Qualifier, parentId: ScopeID?): ObjectScope<LifecycleOwner> {
    val scope = getKoin().createObjectScoped(this, scopeId, qualifier, parentId)
    bindScope(scope)
    return scope
}

/**
 * Bind given scope to current LifecycleOwner
 * @param scope
 * @param event
 */
fun LifecycleOwner.bindScope(scope: Scope, event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY) {
    lifecycle.addObserver(ScopeObserver(event, this, scope))
}

/**
 * Get current Koin scope, bound to current lifecycle
 */
val LifecycleOwner.currentScope: Scope
    get() = getOrCreateCurrentScope()

interface HasParentScope {
    val parentScopeId: ScopeID
}

private val LifecycleOwner.parentScopeId: ScopeID?
    get() = when (this) {
        is HasParentScope -> parentScopeId
        is Fragment -> parentFragment?.resolveScopeId() ?: activity?.resolveScopeId()
        else -> null
    }

private fun LifecycleOwner.resolveScopeId(): ScopeID? =
        try {
            this.currentScope.id
        } catch (e: NoScopeDefinitionFoundException) {
            null
        }
