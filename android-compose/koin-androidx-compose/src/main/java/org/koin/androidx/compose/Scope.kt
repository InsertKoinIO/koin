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
import androidx.compose.runtime.remember
import org.koin.core.Koin
import org.koin.core.scope.Scope

@PublishedApi
internal val LocalScope = compositionLocalOf<Scope?> { null }

@Composable
fun KoinScope(
    getScope: Koin.() -> Scope,
    content: @Composable () -> Unit
) {
    val koin = getKoin()
    val scope = remember {
        koin.getScope()
    }
    CompositionLocalProvider(LocalScope provides scope) {
        content()
    }
}

@Composable
fun getScope() = LocalScope.current