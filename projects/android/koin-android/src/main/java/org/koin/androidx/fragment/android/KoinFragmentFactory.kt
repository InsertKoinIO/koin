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
package org.koin.androidx.fragment.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import org.koin.core.component.KoinComponent
import org.koin.core.scope.Scope

/**
 * FragmentFactory for Koin
 */
class KoinFragmentFactory(private val scope: Scope? = null) : FragmentFactory(), KoinComponent {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val clazz = Class.forName(className).kotlin
        val instance: Fragment? = if (scope != null) {
            scope.getOrNull(clazz)
        } else {
            getKoin().getOrNull(clazz)
        }
        return (instance ?: super.instantiate(classLoader, className))
    }

}