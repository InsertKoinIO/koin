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
package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.android.viewmodel.ViewModelParameters
import org.koin.androidx.viewmodel.ext.koin.createInstance
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
        val params = viewModelParameters
        clear()
        return createInstance(
            params ?: error("Can't getByClass ViewModel from ViewModelFactory with empty parameters"),
            modelClass
        )
    }

    /**
     * clear current call params
     */
    private fun clear() {
        viewModelParameters = null
    }
}