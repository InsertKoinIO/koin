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
package org.koin.android.ext.android

import android.content.ComponentCallbacks
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.KoinScopeComponent
import org.koin.core.context.GlobalContext
import org.koin.core.scope.Scope


@KoinInternalApi
fun ComponentCallbacks.getKoinScope(): Scope {
    return when (this) {
        is AndroidScopeComponent -> scope
        is KoinScopeComponent -> scope
        is KoinComponent -> getKoin().scopeRegistry.rootScope
        else -> GlobalContext.get().scopeRegistry.rootScope
    }
}