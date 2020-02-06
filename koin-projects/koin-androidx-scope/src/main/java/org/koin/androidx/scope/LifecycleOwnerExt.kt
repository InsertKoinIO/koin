/*
 * Copyright 2017-2020 the original author or authors.
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
import org.koin.core.scope.Scope
import org.koin.ext.getScopeId
import org.koin.ext.getScopeName

/**
 * Provide an scope for given LifecycleOwner component
 *
 * @author Arnaud Giuliani
 */

private fun LifecycleOwner.getKoin() = (this as ComponentCallbacks).getKoin()

private fun LifecycleOwner.getOrCreateAndroidScope(): Scope {
    val scopeId = getScopeId()
    return getKoin().getScopeOrNull(scopeId) ?: createAndBindAndroidScope(scopeId, getScopeName())
}

private fun LifecycleOwner.createAndBindAndroidScope(scopeId: String, qualifier: Qualifier): Scope {
    val scope = getKoin().createScope(scopeId, qualifier)
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
val LifecycleOwner.lifecycleScope: Scope
    get() = getOrCreateAndroidScope()

@Deprecated("Use lifecycleScope instead",replaceWith = ReplaceWith("lifecycleScope"),level = DeprecationLevel.ERROR)
val LifecycleOwner.scope: Scope
    get() = error("Don't use scope on a lifecycle component. Use lifecycleScope instead")

@Deprecated("Use lifecycleScope instead",replaceWith = ReplaceWith("lifecycleScope"))
val LifecycleOwner.currentScope: Scope
    get() = getOrCreateAndroidScope()