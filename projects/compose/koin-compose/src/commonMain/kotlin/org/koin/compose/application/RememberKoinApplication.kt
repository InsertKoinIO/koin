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
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi

/**
 * Remember Koin Application to handle its closure if necessary
 *
 * @param koinApplication
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@Composable
inline fun rememberKoinApplication(koinApplication: KoinApplication): Koin {
    val wrapper = remember {
        CompositionKoinApplicationLoader(koinApplication)
    }
    return wrapper.koin ?: error("Koin context has not been initialized in rememberKoinApplication")
}