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

import android.app.Activity
import androidx.activity.ComponentActivity
import org.koin.android.ext.android.getKoin
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.scope.Scope

/**
 * Provide Koin Scope tied to ComponentActivity
 */

fun ComponentActivity.activityScope() = LifecycleScopeDelegate<Activity>(this, this.getKoin())
fun ComponentActivity.activityRetainedScope() = LifecycleViewModelScopeDelegate(this, this.getKoin())
fun ComponentActivity.createScope(source: Any? = null): Scope = getKoin().createScope(getScopeId(), getScopeName(), source)
fun ComponentActivity.getScopeOrNull(): Scope? = getKoin().getScopeOrNull(getScopeId())