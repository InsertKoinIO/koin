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
import org.koin.core.module.Module
import org.koin.dsl.ScopeDSL

/**
 * Declare an Activity scope section.
 *
 * Dependencies declared inside this block are scoped to the Activity lifecycle.
 *
 * ```kotlin
 * activityScope {
 *     scoped { MyActivityDependency() }
 * }
 * ```
 */
fun Module.activityScope(scopeSet: ScopeDSL.() -> Unit) {
    ScopeDSL(ActivityScopeArchetype, this).apply(scopeSet)
}

/**
 * Declare an Activity retained scope section.
 *
 * Dependencies declared inside this block survive configuration changes.
 *
 * ```kotlin
 * activityRetainedScope {
 *     scoped { MyRetainedDependency() }
 * }
 * ```
 */
fun Module.activityRetainedScope(scopeSet: ScopeDSL.() -> Unit) {
    ScopeDSL(ActivityRetainedScopeArchetype, this).apply(scopeSet)
}

/**
 * Declare a Fragment scope section.
 *
 * Dependencies declared inside this block are scoped to the Fragment lifecycle.
 *
 * ```kotlin
 * fragmentScope {
 *     scoped { MyFragmentDependency() }
 * }
 * ```
 */
fun Module.fragmentScope(scopeSet: ScopeDSL.() -> Unit) {
    ScopeDSL(FragmentScopeArchetype, this).apply(scopeSet)
}
