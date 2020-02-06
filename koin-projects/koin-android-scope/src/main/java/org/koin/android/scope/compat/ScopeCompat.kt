/*
 * Copyright 2017-2020 the original author or authors.
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
package org.koin.android.scope.compat

import android.arch.lifecycle.LifecycleOwner
import org.koin.android.scope.lifecycleScope
import org.koin.core.scope.Scope

/**
 * Scope functions to help for ViewModel in Java
 *
 * @author Jeziel Lago
 */
object ScopeCompat {

    @JvmStatic
    fun lifecycleScope(owner: LifecycleOwner): Scope {
        return owner.lifecycleScope
    }
}