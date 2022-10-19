///*
// * Copyright 2017-2021 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */

package org.koin.androidx.viewmodel.scope

import android.os.Bundle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import org.koin.core.annotation.KoinInternalApi


/**
 * Scope extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */
@Deprecated("Replaced by CreationExtras API")
fun emptyState(): BundleDefinition = { Bundle() }

@Deprecated("Replaced by CreationExtras API")
typealias BundleDefinition = () -> Bundle

@KoinInternalApi
@PublishedApi
internal fun Bundle.toExtras(): CreationExtras? {
    return if (keySet().isEmpty()) null
    else {
        val keys = keySet()
        runCatching {
            MutableCreationExtras().also { extras ->
                keys.forEach { key ->
                    extras[key as CreationExtras.Key<String>] = get(key).toString()
                }
            }
        }.getOrNull()
    }
}