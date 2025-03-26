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
@file:OptIn(KoinInternalApi::class)

package org.koin.compose.application

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.compose.composeMultiplatformConfiguration
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.logger.Level
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinApplication

@Composable
@KoinInternalApi
inline fun rememberKoinApplication(noinline koinAppDeclaration: KoinAppDeclaration): Koin {
    val wrapper = remember(koinAppDeclaration) {
        CompositionKoinApplicationLoader(koinApplication(koinAppDeclaration))
    }
    return wrapper.koin ?: error("Koin context has not been initialized in rememberKoinApplication")
}

@OptIn(KoinInternalApi::class)
@Composable
@KoinInternalApi
inline fun rememberKoinMPApplication(configuration: KoinConfiguration, logLevel: Level): Koin {
    val configuration = composeMultiplatformConfiguration(logLevel, config = configuration)
    val wrapper = remember(configuration,logLevel) {
        CompositionKoinApplicationLoader(koinApplication(configuration))
    }
    return wrapper.koin ?: error("Koin context has not been initialized in rememberKoinApplication")
}