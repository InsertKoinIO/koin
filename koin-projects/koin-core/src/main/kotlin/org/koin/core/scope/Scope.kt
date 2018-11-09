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
package org.koin.core.scope

import org.koin.core.Koin
import java.util.*

/**
 * Koin Scope
 * Help declare limited timelife instances
 *
 * @author Arnaud Giuliani
 */
data class Scope internal constructor(val id: String, internal val internalId: String = UUID.randomUUID().toString()) {

    internal var koin: Koin? = null

    /**
     * Is Scope associated to Koin
     */
    fun isRegistered() = koin != null

    /**
     * Close all instances from this scope
     */
    fun close() {
        if (koin == null) error("Can't close Scope $id without any Koin instance associated")
        koin?.closeScope(internalId)
    }

    /**
     * Register in Koin instance
     */
    fun register(koin: Koin) {
        this.koin = koin
    }
}