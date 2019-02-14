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

import android.app.Activity
import android.content.ComponentCallbacks
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.koin.android.ext.android.getKoin
import org.koin.core.scope.ScopeInstance
import org.koin.ext.getFullName

/**
 * LifecycleOwner extensions
 *
 * @author Arnaud Giuliani
 */

/**
 * Set a ScopeInstance Observer onto the actual LifecycleOwner koincomponent
 * will close the bound scopes on lifecycle event
 * @see ScopeObserver
 * @param event : lifecycle event - default ON_DESTROY
 * @param scopes
 */
fun LifecycleOwner.bindScope(scope: ScopeInstance, event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY) {
    lifecycle.addObserver(ScopeObserver(event, this, scope))
}

fun LifecycleOwner.getKoin() = (this as ComponentCallbacks).getKoin()

/**
 * Get or create Scope
 * @param scope Id
 */
fun LifecycleOwner.createScope(id: String, scopeName: String? = null): ScopeInstance {
    return getKoin().createScope(id, scopeName)
}

/**
 * Get Scope
 * @param scope Id
 */
fun LifecycleOwner.getScope(id: String): ScopeInstance {
    return getKoin().getScope(id)
}

/**
 * Detach a Scope
 * @param scope Id
 */
fun LifecycleOwner.deleteScope(id: String) {
    getKoin().deleteScope(id)
}

/**
 * Get or create Scope
 * @param scope Id
 */
fun Activity.getActivityScope(): ScopeInstance {
    return (this as LifecycleOwner).getOrCreateAndroidScope()
}

/**
 * Scope Name for current LifecycleOwner
 */
fun LifecycleOwner.getScopeName() = this::class.getFullName()

/**
 * Scope Id for current LifecycleOwner
 */
fun LifecycleOwner.getScopeId() = this.toString()

private fun LifecycleOwner.getOrCreateAndroidScope(): ScopeInstance {
    val name = getScopeName()
    val scopeId = getScopeId()
    return getKoin().getOrCreateScope(scopeId, name)
}

/**
 * Get or create Scope
 * @param scope Id
 */
fun Fragment.getFragmentScope(): ScopeInstance {
    return (this as LifecycleOwner).getOrCreateAndroidScope()
}