package org.koin.android.ext.android

import android.content.ComponentCallbacks
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope


/**
 * Get Koin context
 */
fun ComponentCallbacks.getKoin(): Koin = GlobalContext.get().koin

/**
 * inject lazily given dependency for Android koincomponent
 * @param qualifier - bean qualifier / optional
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> ComponentCallbacks.inject(
        qualifier: Qualifier? = null,
        scope: Scope = Scope.GLOBAL,
        noinline parameters: ParametersDefinition? = null
) = lazy { get<T>(qualifier, scope, parameters) }

/**
 * get given dependency for Android koincomponent
 * @param name - bean name
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> ComponentCallbacks.get(
        qualifier: Qualifier? = null,
        scope: Scope = Scope.GLOBAL,
        noinline parameters: ParametersDefinition? = null
): T = getKoin().get(qualifier, scope, parameters)