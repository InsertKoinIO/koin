/*
 * Copyright 2017-2020 the original author or authors.
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

package org.koin.androidx.scope

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID

/**
 * ScopeFragment
 *
 * Fragment, allow to create & destroy tied Koin scope
 *
 * @author Arnaud Giuliani
 */
abstract class ScopeFragment(
        @LayoutRes contentLayoutId: Int = 0
) : Fragment(contentLayoutId), KoinScopeComponent {

    private val scopeID: ScopeID by lazy { getScopeId() }
    override val koin by lazy { getKoin() }
    override val scope: Scope by lazy { koin.createScope(scopeID, getScopeName(), this) }

    val scopeActivity : ScopeActivity? by lazy { activity as? ScopeActivity }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        koin.logger.debug("Open fragment scope: $scope")
    }

    override fun onDestroy() {
        super.onDestroy()

        koin.logger.debug("Close fragment scope: $scope")
        scope.close()
    }

    /**
     * inject lazily
     * @param qualifier - bean qualifier / optional
     * @param parameters - injection parameters
     */
    inline fun <reified T : Any> inject(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
    ) = lazy(LazyThreadSafetyMode.NONE) { get<T>(qualifier, parameters) }

    /**
     * get given dependency
     * @param name - bean name
     * @param parameters - injection parameters
     */
    inline fun <reified T : Any> get(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
    ): T = scope.get(qualifier, parameters)
}