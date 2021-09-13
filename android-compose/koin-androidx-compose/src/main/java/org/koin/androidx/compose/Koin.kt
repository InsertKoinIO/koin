/*
 * Copyright 2017-2021 the original author or authors.
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
package org.koin.androidx.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

@PublishedApi
internal val LocalKoin = compositionLocalOf { GlobalContext.get() }

@Composable
fun Koin(
    appDeclaration: KoinAppDeclaration? = null,
    content: @Composable () -> Unit
) {
    val koinApplication = koinApplication(appDeclaration)
    CompositionLocalProvider(LocalKoin provides koinApplication.koin) {
        content()
    }
}

@Composable
fun getKoin(): Koin = LocalKoin.current