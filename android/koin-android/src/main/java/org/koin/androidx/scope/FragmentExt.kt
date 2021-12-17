/*
 * Copyright 2017-2021 the original author or authors.
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

import androidx.fragment.app.Fragment
import org.koin.android.ext.android.getKoin
import org.koin.core.Koin
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.scope.Scope

/**
 * Provide scope tied to Fragment
 */
fun Fragment.fragmentScope() = LifecycleScopeDelegate<Fragment>(this,this.getKoin()){ koin: Koin ->
    val scope = koin.createScope(getScopeId(), getScopeName())
    val activityScope = activity?.getScopeOrNull()
    activityScope?.let { scope.linkTo(it) }
    scope
}

fun Fragment.createScope(source: Any? = null): Scope = getKoin().createScope(getScopeId(), getScopeName(), source)

fun Fragment.getScopeOrNull(): Scope? = getKoin().getScopeOrNull(getScopeId())

val Fragment.scopeActivity: ScopeActivity? get() = activity as? ScopeActivity
inline fun <reified T : ScopeActivity> Fragment.requireScopeActivity(): T = activity as? T ?: error("can't get ScopeActivity for class ${T::class}")