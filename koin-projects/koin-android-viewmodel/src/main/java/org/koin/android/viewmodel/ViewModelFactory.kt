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
import org.koin.android.viewmodel.ext.koin.get
import org.koin.android.viewmodel.ext.koin.getByName
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.standalone.KoinComponent


/**
 * Koin ViewModel factory
 *
 * @author Arnaud Giuliani
 */
object ViewModelFactory : ViewModelProvider.Factory, KoinComponent {

    var viewModelParameters: ViewModelParameters? = null

    /**
     * Create instance for ViewModelProvider Factory
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (viewModelParameters != null) {
            // Get params to pass to factory
            val name = viewModelParameters?.name
            val module = viewModelParameters?.module
            val params = viewModelParameters?.parameters ?: emptyParameterDefinition()
            // Clear local stuff
            clear()

            if (name != null) {
                getByName(name, module, params)
            } else get(modelClass, module, params)
        } else error("Can't get ViewModel from ViewModelFactory with empty parameters")
    }

    /**
     * clear current call params
     */
    private fun clear() {
        viewModelParameters = null
    }
}

/**
 * Data holder for ViewModel Factory
 */
data class ViewModelParameters(
    val name: String? = null,
    val module: String? = null,
    val parameters: ParameterDefinition
)