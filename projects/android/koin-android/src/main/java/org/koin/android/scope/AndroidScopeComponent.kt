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
package org.koin.android.scope

import org.koin.core.scope.Scope

/**
 * Android Component that can handle a Koin Scope
 */
interface AndroidScopeComponent {

    //TODO Make scope nullable with var?

    /**
     * Current Scope in use by the component
     */
    val scope: Scope

    /**
     * Called before closing a scope, on onDestroy
     */
    fun onCloseScope(){}
}