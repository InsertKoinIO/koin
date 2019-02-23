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
package org.koin.core.definition

import org.koin.core.error.MissingPropertyException
import java.util.concurrent.ConcurrentHashMap

/**
 * Definitions Properties
 *
 * @author Arnaud Giuliani
 */
data class Properties(private val data: MutableMap<String, Any> = ConcurrentHashMap()) {

    operator fun <T> set(key: String, value: T) {
        data[key] = value as Any
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getOrNull(key: String): T? {
        return data[key] as? T
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: String): T {
        return data[key] as? T ?: throw MissingPropertyException("missing property for '$key'")
    }
}