package org.koin.ext

import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeDefinition
import org.koin.dsl.ScopeDSL
import org.koin.java.KoinJavaComponent

//TODO Check Enum Class / Name -> Qualifier
//TODO Refactor Android Scope - change getOrCreateLifecycleScope

inline fun <reified T> Module.scope(scopeSet: ScopeDSL.() -> Unit) {
    val scopeDefinition = ScopeDefinition(TypeQualifier(T::class))
    ScopeDSL(scopeDefinition).apply(scopeSet)
    otherScopes.add(scopeDefinition)
}

fun <T : Any> T.getScopeName() = TypeQualifier(this::class)
fun <T : Any> T.getScopeId() = this::class.getFullName() + "@" + System.identityHashCode(this)

fun <T : Any> T.getOrCreateScope(): Scope {
    val scopeId = getScopeId()
    return GlobalContext.get().getScopeOrNull(scopeId) ?: createScope(scopeId, getScopeName())
}

fun <T : Any> T.getScopeOrNull(): Scope? {
    return GlobalContext.get().getScopeOrNull(getScopeId())
}

fun <T : Any> T.createScope(): Scope {
    val scopeId = getScopeId()
    return createScope(scopeId, getScopeName())
}

fun <T : Any> T.getOrCreateScope(koin: Koin): Scope {
    val scopeId = getScopeId()
    return koin.getScopeOrNull(scopeId) ?: createScope(scopeId, getScopeName())
}

fun <T : Any> T.getScopeOrNull(koin: Koin): Scope? {
    val scopeId = getScopeId()
    return koin.getScopeOrNull(scopeId)
}

fun <T : Any> T.createScope(koin: Koin): Scope {
    val scopeId = getScopeId()
    return koin.createScope(scopeId, getScopeName())
}

private fun <T : Any> T.createScope(scopeId: String, qualifier: Qualifier): Scope {
    val scope = KoinJavaComponent.getKoin().createScope(scopeId, qualifier)
    return scope
}

fun <T : Any> T.closeScope() {
    val scope = getOrCreateScope()
    scope.close()
    GlobalContext.get().deleteScope(getScopeId())
}