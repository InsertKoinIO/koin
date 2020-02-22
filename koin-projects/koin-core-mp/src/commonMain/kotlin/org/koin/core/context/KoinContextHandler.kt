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
package org.koin.core.context

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.state.MainIsolatedState
import org.koin.mp.mpsynchronized

/**
 * Help hold any implementation of KoinContext
 */
object KoinContextHandler {

    private data class KoinContextNullable(var context: KoinContext?)

    private val contextState = MainIsolatedState(KoinContextNullable(null))
    private var _context: KoinContext?
        get() = contextState._get().context
        set(value) {
            contextState._get().context = value
        }

    private fun getContext(): KoinContext {
        return _context ?: error("No Koin Context configured. Please use startKoin or koinApplication DSL. ")
    }

    /**
     * Retrieve current KoinContext
     */
    fun get(): Koin = getContext().get()

    /**
     * Retrieve current KoinContext or null
     */
    fun getOrNull(): Koin? = _context?.getOrNull()

    /**
     * Register new KoinContext
     *
     * @throws IllegalStateException if already registered
     */
    fun register(koinContext: KoinContext) = mpsynchronized(this) {
        if (_context != null) {
            error("A KoinContext is already started")
        }
        _context = koinContext
    }

    /**
     * Start a Koin Application on current KoinContext
     */
    fun start(koinApplication: KoinApplication) {
        getContext().setup(koinApplication)
    }

    /**
     * Stop current KoinContext & clear it
     */
    fun stop() {
        _context?.stop()
        _context = null
    }

}