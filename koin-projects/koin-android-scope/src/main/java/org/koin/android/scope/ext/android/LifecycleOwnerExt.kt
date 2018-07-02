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

package org.koin.android.scope.ext.android

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import org.koin.android.scope.ScopeObserver

/**
 * LifecycleOwner extensions
 *
 * @author Arnaud Giuliani
 */

/**
 * Set a Scope Observer onto the actual LifecycleOwner component
 *
 * @see ScopeObserver
 * @param event : lifecycle event - default ON_DESTROY
 * @param modules : modules names
 */
fun LifecycleOwner.scopedWith(modules: List<String>, event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY) {
    lifecycle.addObserver(ScopeObserver(event, this.javaClass.canonicalName, modules))
}

/**
 * Set a Scope Observer onto the actual LifecycleOwner component
 *
 * @see ScopeObserver
 * @param event : lifecycle event - default ON_DESTROY
 * @param module : module names
 */
fun LifecycleOwner.scopedWith(module: String, event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY) {
    lifecycle.addObserver(ScopeObserver(event, this.javaClass.canonicalName, listOf(module)))
}
