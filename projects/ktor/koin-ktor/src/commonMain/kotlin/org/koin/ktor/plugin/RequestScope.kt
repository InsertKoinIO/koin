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
package org.koin.ktor.plugin

import io.ktor.server.application.ApplicationCall
import org.koin.core.Koin
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.time.Clock

/**
 * Request Scope Holder
 *
 * @author Arnaud Giuliani
 * @author Loïc Favreliere
 */
@OptIn(ExperimentalAtomicApi::class)
class RequestScope(private val _koin: Koin, call: ApplicationCall) : KoinScopeComponent {
    private val scopeId = "request_" + counter.incrementAndFetch()
    override fun getKoin(): Koin = _koin
    override val scope = createScope(scopeId = scopeId, source = call)

    private companion object {
        // Monotonic counter seeded with the current time, for process-unique request scope ids
        private val counter = AtomicLong(Clock.System.now().toEpochMilliseconds())
    }
}