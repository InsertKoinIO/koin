/*
 * Copyright 2017-2018 the original author or authors.
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
package org.koin.android.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import org.koin.android.viewmodel.ext.koin.createInstance
import org.koin.core.parameter.ParameterDefinition
import org.koin.standalone.KoinComponent
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference


/**
 * Koin ViewModel factory
 *
 * @author Arnaud Giuliani
 */
object ViewModelFactory : ViewModelProvider.Factory, KoinComponent {

    private var viewModelParameters: WeakReference<ViewModelParameters>? = null

    /**
     * Create instance for ViewModelProvider Factory
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val params = viewModelParameters?.get()
        clear()
        return createInstance(
            params
                ?: error("Can't getByClass ViewModel from ViewModelFactory with empty parameters"),
            modelClass
        )
    }

    /**
     * clear current call params
     */
    private fun clear() {
        viewModelParameters?.enqueue()
        viewModelParameters = null
    }

    fun postParameters(name: String?, parameters: ParameterDefinition) {
        viewModelParameters = WeakReference(ViewModelParameters(name, parameters), ReferenceQueue())
    }
}

