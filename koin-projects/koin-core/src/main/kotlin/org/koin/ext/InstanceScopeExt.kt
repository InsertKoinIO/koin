package org.koin.ext

import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope

fun <T : Any> T.getScopeName() = TypeQualifier(this::class)
fun <T : Any> T.getScopeId() = this::class.getFullName() + "@" + System.identityHashCode(this)

val <T : Any> T.scope: Scope
    get() = getOrCreateScope()

fun <T : Any> T.getOrCreateScope(): Scope {
    val koin = GlobalContext.get()
    return getScopeOrNull(koin) ?: createScope(koin)
}

fun <T : Any> T.getOrCreateScope(koin: Koin): Scope {
    val scopeId = getScopeId()
    return koin.getScopeOrNull(scopeId) ?: koin.createScope(scopeId, getScopeName())
}

private fun <T : Any> T.getScopeOrNull(koin: Koin = GlobalContext.get()): Scope? {
    val scopeId = getScopeId()
    return koin.getScopeOrNull(scopeId)
}

private fun <T : Any> T.createScope(): Scope {
    return GlobalContext.get().createScope(getScopeId(), getScopeName())
}

private fun <T : Any> T.createScope(koin: Koin): Scope {
    return koin.createScope(getScopeId(), getScopeName())
}