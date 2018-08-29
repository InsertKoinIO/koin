/*
 * Copyright 2017-2018 the original author or authors.
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
package org.koin.androidx.scope.ext.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.koin.androidx.scope.ScopeObserver


/**
 * Set a Scope Observer onto the actual LifecycleOwner component
 *
 * @author Arnaud Giuliani
 *
 * @see ScopeObserver
 * @param event : lifecycle event - default ON_DESTROY
 * @param module : module name
 */
fun LifecycleOwner.scopedWith(module: String, event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY) {
    lifecycle.addObserver(ScopeObserver(event, this, module))
}

