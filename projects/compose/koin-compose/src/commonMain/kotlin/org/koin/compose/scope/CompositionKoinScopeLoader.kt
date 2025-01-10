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
package org.koin.compose.scope

import androidx.compose.runtime.RememberObserver
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.scope.Scope

@KoinExperimentalAPI
@KoinInternalApi
class CompositionKoinScopeLoader(
    val scope: Scope
) : RememberObserver {

    override fun onRemembered() {
        // Nothing to do
    }

    override fun onForgotten() {
        close()
    }

    override fun onAbandoned() {
        close()
    }

    private fun close() {
        if (!scope.isRoot && !(scope.closed)){
            scope.logger.debug("CompositionKoinScopeLoader close scope: '${scope.id}'")
            scope.close()
        }
    }
}