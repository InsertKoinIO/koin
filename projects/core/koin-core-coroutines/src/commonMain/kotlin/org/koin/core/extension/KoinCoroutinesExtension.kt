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

import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.coroutine.KoinCoroutinesEngine
import org.koin.core.coroutine.KoinCoroutinesEngine.Companion.EXTENSION_NAME

/**
 * @author Arnaud Giuliani
 */

/**
 * Register KoinCoroutinesEngine extension
 */
@OptIn(KoinInternalApi::class)
fun KoinApplication.coroutinesEngine(dispatcher : CoroutineDispatcher? = null) {
    with(koin.extensionManager) {
        if (getExtensionOrNull<KoinCoroutinesEngine>(EXTENSION_NAME) == null) {
            registerExtension(EXTENSION_NAME, KoinCoroutinesEngine(dispatcher))
        }
    }
}

@OptIn(KoinInternalApi::class)
val Koin.coroutinesEngine: KoinCoroutinesEngine get() = extensionManager.getExtension(EXTENSION_NAME)
