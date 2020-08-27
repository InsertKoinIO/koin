package org.koin.core.scope

import org.koin.core.Koin
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.ext.getFullName

/**
 * Koin Scope Component
 *
 * Help bring Scope API = Create/Destroy Scope for the given object
 */
interface KoinScopeComponent {

    /**
     * As default implmementation can be
     * " override val scope: Scope by lazy { createScope() } "
     */
    val scope: Scope
    val koin: Koin
    fun getScopeName() = TypeQualifier(this::class)
    fun getScopeId() = this::class.getFullName() + "@" + System.identityHashCode(this)
    fun createScope(): Scope = koin.createScope(getScopeId(), getScopeName(), this)
    fun createScope(scopeID: ScopeID, scopeName: Qualifier, source: Any? = null): Scope = koin.createScope(
        scopeID, scopeName, source)

    fun destroyScope() = scope.close()
}