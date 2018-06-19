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

    /**
     * Current Parameters
     */
    internal var _parameters: ParameterDefinition = emptyParameterDefinition()

    /**
     * Current BeanDefinition name
     */
    internal var _name: String? = null

    /**
     * Module Path
     */
    internal var _module: String? = null

    /**
     * Create instance for ViewModelProvider Factory
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val beanName = _name
        return if (beanName != null) {
            getByName(beanName, _module, _parameters)
        } else get(modelClass, _module, _parameters)
    }
}