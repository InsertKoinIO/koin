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
package org.koin.compose.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.compose.KoinIsolatedContext
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication

/**
 * Starts a new isolated KoinApplication for the provided list of Koin modules.
 * Can be used if you have multiple compose previews in one file that require certain definitions.
 *
 * @param modules list of definitions required for the preview
 * @param content compose preview content
 *
 * @author Yanneck Reiß
 */
@Composable
fun KoinPreviewApplication(
    modules: () -> List<Module>,
    content: @Composable () -> Unit
) {
    var isInitialComposition: Boolean by remember { mutableStateOf(true) }
    var koinApplicationHolder: DynamicIsolatedContextHolder = remember { DynamicIsolatedContextHolder(modules()) }

    KoinIsolatedContext(
        context = koinApplicationHolder.koinApp,
        content = {

            // Here we access the KoinPreviewApplication when we call stopKoin() because the KoinContext composable
            // provides it in the CompositionLocal
            DisposableEffect(modules()) {
                // For the first run of this effect we don't want to do anything
                if (!isInitialComposition) {
                    // Otherwise we stop the current KoinContext which refers to the provided
                    // Koin instance from our koinApplicationHolder, stop and recreate it
                    // with the fresh list of modules
                    stopKoin()
                    koinApplicationHolder = DynamicIsolatedContextHolder(modules())
                }
                // First run has been done, next time the list of modules change,
                // we actually want to reinitialize the KoinApplication
                isInitialComposition = false

                // If the preview disposes, we stop the KoinContext
                onDispose {
                    stopKoin()
                }
            }

            // The preview content composable
            content()
        }
    )
}

/**
 * Isolated context holder that takes list of definitions to dynamically
 * create a KoinApplication with the required definitions.
 *
 * @param modules list of definitions
 *
 * @author Yanneck Reiß
 */
private class DynamicIsolatedContextHolder(
    modules: List<Module>
) {

    val koinApp: KoinApplication = koinApplication(
        appDeclaration = { modules(modules) }
    )
}
