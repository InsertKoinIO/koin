/*
 * Copyright 2017-2023 the original author or authors.
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

import android.content.ComponentCallbacks
import org.koin.android.ext.android.getKoin
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.scope.Scope


inline fun <reified T : ComponentCallbacks> T.createScope(source: Any? = null): Scope {
    return getKoin().createScope(getScopeId(), getScopeName(), source)
}

inline fun <reified T : ComponentCallbacks> T.getScopeOrNull(): Scope? {
    return getKoin().getScopeOrNull(getScopeId())
}

inline fun <reified T : ComponentCallbacks> T.newScope() = lazy { createScope() }
inline fun <reified T: ComponentCallbacks> T.getOrCreateScope() = lazy { getScopeOrNull() ?: createScope() }
