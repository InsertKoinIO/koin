package org.koin.core.scope

import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.ext.getFullName

/**
 * Koin Scope Component
 *
 * Help bring Scope API = Create/Destroy Scope for the given object
 */
@OptIn(KoinApiExtension::class)
interface KoinScopeComponent : KoinComponent {

    val scope: Scope

    fun closeScope() {
        scope.close()
    }
}

fun <T : KoinScopeComponent> T.getScopeId() = this::class.getFullName() + "@" + System.identityHashCode(this)
fun <T : KoinScopeComponent> T.getScopeName() = TypeQualifier(this::class)
fun <T : KoinScopeComponent> T.newScope(source: Any? = null): Scope {
    return getKoin().createScope(getScopeId(), getScopeName(), source)
}

/**
 * inject lazily
 * @param qualifier - bean qualifier / optional
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> KoinScopeComponent.inject(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
) = lazy { get<T>(qualifier, parameters) }

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