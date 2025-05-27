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
package org.koin.core.option

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.registry.OptionRegistry

/**
 * Koin Options to be activated
 */
enum class KoinOption {
    VIEWMODEL_SCOPE_FACTORY
}

fun viewModelScopeFactory(): Pair<KoinOption, Boolean> {
    return KoinOption.VIEWMODEL_SCOPE_FACTORY to true
}

@KoinInternalApi
fun OptionRegistry.hasViewModelScopeFactory() : Boolean = getOrNull<Boolean>(KoinOption.VIEWMODEL_SCOPE_FACTORY) == true