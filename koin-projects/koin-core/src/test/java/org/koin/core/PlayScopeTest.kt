package org.koin.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeDefinition
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module
import org.koin.ext.getFullName
import org.koin.java.KoinJavaComponent.getKoin

class PlayScopeTest {

    @Test
    fun `play with scope`() {
        val koin = startKoin {
            modules(module {
                single { A() }
                scope<A> {
                    scoped { B() }
                    scoped { C() }
                }
            })
        }.koin

        val a = koin.get<A>()
        val b1 = koin.getOrNull<B>()
        assertNull(b1)
        assertNull(koin.getOrNull<C>())

        val scopeForA = a.getOrCreateScope()

        assertNotNull(scopeForA.get<B>())
        assertNotNull(scopeForA.get<C>())

        a.closeScope()

        assertNull(scopeForA.getOrNull<B>())
        assertNull(scopeForA.getOrNull<C>())

        val scopeForA2 = a.getOrCreateScope()

        val b2 = scopeForA2.getOrNull<B>()
        assertNotNull(b2)
        assertNotEquals(b1, b2)
    }

}


//TODO Check for String naming?

//TODO Check Enum Class / Name -> Qualifier

//TODO Refactor Android Scope - change getOrCreateLifecycleScope

inline fun <reified T> Module.scope(scopeSet: ScopeDSL.() -> Unit) {
    val scopeDefinition = ScopeDefinition(TypeQualifier(T::class))
    ScopeDSL(scopeDefinition).apply(scopeSet)
    otherScopes.add(scopeDefinition)
}

fun <T : Any> T.getScopeName() = TypeQualifier(this::class)

fun <T : Any> T.getScopeId() = this::class.getFullName() + "@" + System.identityHashCode(this)

// TODO Decline API - get, Create, getOrNull

fun <T : Any> T.getOrCreateScope(): Scope {
    val scopeId = getScopeId()
    return GlobalContext.get().getScopeOrNull(scopeId) ?: createScope(scopeId, getScopeName())
}

fun <T : Any> T.getOrCreateScope(koin: Koin): Scope {
    val scopeId = getScopeId()
    return koin.getScopeOrNull(scopeId) ?: createScope(scopeId, getScopeName())
}

private fun <T : Any> T.createScope(scopeId: String, qualifier: Qualifier): Scope {
    val scope = getKoin().createScope(scopeId, qualifier)
    return scope
}

fun <T : Any> T.closeScope() {
    val scope = getOrCreateScope()
    scope.close()
    GlobalContext.get().deleteScope(getScopeId())
}