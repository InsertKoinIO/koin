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

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.ext.getFullName

/**
 * Provide Koin Scope tied to ComponentActivity
 */
fun <T : ComponentActivity> T.activityScope() : Scope {
    val scopeViewModel = viewModels<ScopeHandlerViewModel>().value
    if (scopeViewModel.scope == null){
        scopeViewModel.scope = newScope()
    }
    return scopeViewModel.scope!!
}

fun <T : ComponentActivity> T.getScopeId() = this::class.getFullName() + "@" + System.identityHashCode(this)
fun <T : ComponentActivity> T.getScopeName() = TypeQualifier(this::class)

/**
 * Create new scope
 */
fun <T : ComponentActivity> T.newScope(source: Any? = null): Scope {
    return getKoin().createScope(getScopeId(), getScopeName(), source)
}