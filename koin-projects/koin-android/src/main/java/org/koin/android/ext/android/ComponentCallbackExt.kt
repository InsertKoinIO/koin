package org.koin.android.ext.android

import android.content.ComponentCallbacks
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.ScopeInstance


/**
 * Get Koin context
 */
fun ComponentCallbacks.getKoin(): Koin = GlobalContext.get().koin

/**
 * inject lazily given dependency for Android koincomponent
 * @param name - bean name / optional
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> ComponentCallbacks.inject(
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
inline fun <reified T : Any> ComponentCallbacks.get(
        name: String = "",
        scope: ScopeInstance = ScopeInstance.GLOBAL,
        noinline parameters: ParametersDefinition? = null
): T = getKoin().get(name, scope, parameters)