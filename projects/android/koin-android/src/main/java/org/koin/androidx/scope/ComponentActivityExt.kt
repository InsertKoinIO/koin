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
package org.koin.androidx.scope

import android.content.ComponentCallbacks
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeCallback

/**
 * Create Koin Scope for Activity
 */
fun ComponentActivity.activityScope() = lazy { createActivityScope() }

/**
 * Create Koin Scope for Activity - Retained Scope
 */
fun ComponentActivity.activityRetainedScope() = lazy { createActivityRetainedScope() }

@KoinInternalApi
fun ComponentActivity.createScope(source: Any? = null): Scope = getKoin().createScope(getScopeId(), getScopeName(), source)
fun ComponentActivity.getScopeOrNull(): Scope? = getKoin().getScopeOrNull(getScopeId())


/**
 * Create Scope for AppCompatActivity, given it's extending AndroidScopeComponent.
 * Also register it in AndroidScopeComponent.scope
 */
fun ComponentActivity.createActivityScope(): Scope {
    if (this !is AndroidScopeComponent) {
        error("Activity should implement AndroidScopeComponent")
    }
    return getKoin().getScopeOrNull(getScopeId()) ?: createScopeForCurrentLifecycle(this, ActivityScopeArchetype)
}

internal fun ComponentCallbacks.createScopeForCurrentLifecycle(owner: LifecycleOwner, scopeArchetype : TypeQualifier): Scope {
    val scope = getKoin().createScope(getScopeId(), getScopeName(), this, scopeArchetype)
    scope.registerCallback(object : ScopeCallback{
        override fun onScopeClose(scope: Scope) {
            (owner as AndroidScopeComponent).onCloseScope()
        }
    })
    owner.registerScopeForLifecycle(scope)
    return scope
}

internal fun LifecycleOwner.registerScopeForLifecycle(
    scope: Scope
) {
    lifecycle.addObserver(
        object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                scope.close()
                super.onDestroy(owner)
            }
        }
    )
}

/**
 * Create Retained Scope for AppCompatActivity, given it's extending AndroidScopeComponent.
 * Also register it in AndroidScopeComponent.scope
 */
fun ComponentActivity.createActivityRetainedScope(): Scope {
    if (this !is AndroidScopeComponent) {
        error("Activity should implement AndroidScopeComponent")
    }

    val scopeViewModel = viewModels<ScopeHandlerViewModel>().value
    if (scopeViewModel.scope == null) {
        val scope = getKoin().createScope(getScopeId(), getScopeName(), scopeArchetype = ActivityRetainedScopeArchetype)
        scopeViewModel.scope = scope
    }
    return scopeViewModel.scope!!
}