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
package org.koin.androidx.scope.dsl

import org.koin.androidx.scope.ActivityRetainedScopeArchetype
import org.koin.androidx.scope.ActivityScopeArchetype
import org.koin.androidx.scope.FragmentScopeArchetype
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.ScopeDSL

fun Module.activityScope(scopeSet: ScopeDSL.() -> Unit) {
    val qualifier = ActivityScopeArchetype
    ScopeDSL(qualifier, this).apply(scopeSet)
}

fun Module.activityRetainedScope(scopeSet: ScopeDSL.() -> Unit) {
    val qualifier = ActivityRetainedScopeArchetype
    ScopeDSL(qualifier, this).apply(scopeSet)
}

fun Module.fragmentScope(scopeSet: ScopeDSL.() -> Unit) {
    val qualifier = FragmentScopeArchetype
    ScopeDSL(qualifier, this).apply(scopeSet)
}
