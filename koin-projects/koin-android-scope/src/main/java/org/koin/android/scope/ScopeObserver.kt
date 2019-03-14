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

package org.koin.android.scope

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.KoinComponent
import org.koin.core.scope.Scope

/**
 * Observe a LifecycleOwner
 *
 * @author Arnaud Giuliani
 *
 * release module instances from signals : ON_STOP, ON_DESTROY
 */
class ScopeObserver(val event: Lifecycle.Event, val target: Any, val scope: Scope) :
        LifecycleObserver, KoinComponent {

    /**
     * Handle ON_STOP to release Koin modules
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        if (event == Lifecycle.Event.ON_STOP) {
            logger.debug("$target received ON_STOP")
            scope.close()
        }
    }

    /**
     * Handle ON_DESTROY to release Koin modules
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        if (event == Lifecycle.Event.ON_DESTROY) {
            logger.debug("$target received ON_DESTROY")
            scope.close()
        }
    }
}
