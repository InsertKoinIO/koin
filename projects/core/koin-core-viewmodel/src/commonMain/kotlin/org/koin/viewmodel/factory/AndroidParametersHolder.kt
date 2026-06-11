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
package org.koin.viewmodel.factory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.ParametersHolder
import kotlin.reflect.KClass

class AndroidParametersHolder(
    initialValues: ParametersDefinition? = null,
    private val extras: CreationExtras,
) : ParametersHolder(initialValues?.invoke()?.values?.toMutableList() ?: mutableListOf()) {

    override fun <T> elementAt(i: Int, clazz: KClass<*>): T {
        return createSavedStateHandleOrElse(clazz) { super.elementAt(i, clazz) }
    }

    override fun <T> getOrNull(clazz: KClass<*>): T? {
        return createSavedStateHandleOrElse(clazz) { super.getOrNull(clazz) }
    }

    private inline fun <T> createSavedStateHandleOrElse(clazz: KClass<*>, block: () -> T): T {
        return if (clazz == SavedStateHandle::class) {
            try {
                extras.createSavedStateHandle() as T
            } catch (e: IllegalArgumentException) {
                // androidx throws "CreationExtras must have a value by SAVED_STATE_REGISTRY_OWNER_KEY"
                // when the ViewModel's CreationExtras has no SavedStateRegistryOwner (#2417). Surface
                // an actionable Koin message instead of the raw androidx error.
                throw IllegalStateException(
                    "Koin could not create a SavedStateHandle: the ViewModel's CreationExtras has no SavedStateRegistryOwner. " +
                        "Resolve the ViewModel via koinViewModel()/koinNavViewModel() with a proper owner (e.g. a NavBackStackEntry), " +
                        "and inject SavedStateHandle directly in the ViewModel constructor (not lazily/outside construction).",
                    e,
                )
            }
        } else block()
    }
}