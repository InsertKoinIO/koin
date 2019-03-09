package org.koin.android.ext.android

import android.app.Application
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.ScopeInstance

/**
 * ComponentCallbacks extensions for Android
 *
 * @author Arnaud Giuliani
 */

/**
 * inject lazily given dependency for Android koincomponent
 * @param name - bean name / optional
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> Application.inject(
        name: String = "",
        scope: ScopeInstance = ScopeInstance.GLOBAL,
        noinline parameters: ParametersDefinition? = null
) = lazy { get<T>(name, scope, parameters) }

/**
 * get given dependency for Android koincomponent
 * @param name - bean name
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> Application.get(
        name: String = "",
        scope: ScopeInstance = ScopeInstance.GLOBAL,
        noinline parameters: ParametersDefinition? = null
): T = getKoin().get(name, scope, parameters)
