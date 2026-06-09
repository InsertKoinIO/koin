package org.koin.core

import org.koin.Simple
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.getScopeId
import org.koin.core.logger.Level
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.ScopeDSL
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Regression repro for issue #2379 (archetype-scope variant).
 *
 * The plain named-scope case is covered by DeclareInstanceTest. The case still
 * reported failing in 4.2.0/4.2.1 (dees91) uses an *archetype* scope
 * (activityRetainedScope): a scoped definition resolved through its archetype,
 * whose transitive get() for a declared dependency falls through to _root_.
 */
class ArchetypeDeclareResolveTest {

    open class Archetype
    class ArchetypeExt : Archetype()

    @KoinDslMarker
    fun Module.scopeArchetype(scopeSet: ScopeDSL.() -> Unit) {
        val qualifier = TypeQualifier(Archetype::class)
        ScopeDSL(qualifier, this).apply(scopeSet)
    }

    @OptIn(KoinInternalApi::class)
    @Test
    fun archetype_scope_resolves_declared_transitive_dependency_issue_2379() {
        val scopeModule = module {
            scopeArchetype {
                scoped { Simple.ComponentB(get()) }
            }
        }
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(scopeModule)
        }.koin

        val archetypeExt = ArchetypeExt()
        val scope = koin.createScope<ArchetypeExt>(
            archetypeExt.getScopeId(), archetypeExt,
            TypeQualifier(Archetype::class)
        )
        scope.declare(Simple.ComponentA())

        // declared dependency must resolve from the scope it was declared in
        val a = scope.getOrNull<Simple.ComponentA>()
        assertNotNull(a)

        // scoped ComponentB's transitive get() for ComponentA must resolve in-scope, not _root_
        val b = scope.get<Simple.ComponentB>()
        assertEquals(a, b.a)
    }

    // dees91 minimal shape: a ROOT factory depends on a SCOPED dependency,
    // resolved transitively from the scope. v2 switches context to root when it
    // finds the root factory via linked scopes, then can't resolve the scoped dep.
    class Connector
    class Interactor(val connector: Connector)

    @OptIn(KoinInternalApi::class)
    @Test
    fun root_factory_depending_on_scoped_dep_resolves_in_scope_issue_2379() {
        val scopeModule = module {
            scopeArchetype {
                scoped { Connector() }
            }
            factory { Interactor(get()) } // ROOT factory needs the scoped Connector
        }
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(scopeModule)
        }.koin

        val archetypeExt = ArchetypeExt()
        val scope = koin.createScope<ArchetypeExt>(
            archetypeExt.getScopeId(), archetypeExt,
            TypeQualifier(Archetype::class)
        )

        // resolving the root factory FROM the scope must let its scoped dep resolve in-scope
        val interactor = scope.get<Interactor>()
        assertNotNull(interactor.connector)
    }
}
