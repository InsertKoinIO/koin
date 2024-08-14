/*
 * Copyright 2017-Present the original author or authors.
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
package org.koin.core.extension

import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi

/**
 * Koin ExtensionManager - Allow to run new additional features, by adding "Extension"
 *
 * @see KoinExtension
 *
 * @author Arnaud Giuliani
 */
@KoinInternalApi
class ExtensionManager(internal val _koin: Koin) {

    @PublishedApi
    internal val extensions = hashMapOf<String, KoinExtension>()

    inline fun <reified T : KoinExtension> getExtension(id: String): T = getExtensionOrNull(id) ?: error("Koin extension '$id' not found.")

    inline fun <reified T : KoinExtension> getExtensionOrNull(id: String): T? = extensions[id] as? T

    fun <T : KoinExtension> registerExtension(id: String, extension: T) {
        extensions[id] = extension
        extension.onRegister(_koin)
    }

    fun close() {
        extensions.values.forEach { it.onClose() }
    }
}
