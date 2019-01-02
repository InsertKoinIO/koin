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
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
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
fun AppCompatActivity.getActivityScope(): ScopeInstance {
    return (this as LifecycleOwner).getOrCreateAndroidScope()
}

private fun LifecycleOwner.getOrCreateAndroidScope(): ScopeInstance {
    val name = this::class.getFullName()
    val scopeId = this.toString()
    return getKoin().getOrCreateScope(scopeId, name)
}

/**
 * Get or create Scope
 * @param scope Id
 */
fun Fragment.getFragmentScope(): ScopeInstance {
    return (this as LifecycleOwner).getOrCreateAndroidScope()
}