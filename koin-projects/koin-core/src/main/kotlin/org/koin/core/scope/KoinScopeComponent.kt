package org.koin.core.scope

import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.ext.getFullName

/**
 * Koin Scope Component
 *
 * Help bring Scope API = Create/Destroy Scope for the given object
 */
interface KoinScopeComponent {

    val koin: Koin
    val scope: Scope

    fun destroyScope() {
        scope.close()
    }
}

fun <T : KoinScopeComponent> T.getScopeId() = this::class.getFullName() + "@" + System.identityHashCode(this)
fun <T : KoinScopeComponent> T.getScopeName() = TypeQualifier(this::class)
fun <T : KoinScopeComponent> T.createScope(source: Any? = null): Scope {
    return koin.createScope(getScopeId(), getScopeName(), source)
}

class KoinScopeComponentDelegate(
        override val koin: Koin
) : KoinScopeComponent {

    override val scope: Scope by lazy { createScope() }
}

fun koinScopeDelegate(koin: Koin) = KoinScopeComponentDelegate(koin)

/**
 * inject lazily
 * @param qualifier - bean qualifier / optional
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> KoinScopeComponent.inject(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
) = lazy(LazyThreadSafetyMode.NONE) { get<T>(qualifier, parameters) }

/**
 * get given dependency
 * @param name - bean name
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> KoinScopeComponent.get(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): T = scope.get(qualifier, parameters)