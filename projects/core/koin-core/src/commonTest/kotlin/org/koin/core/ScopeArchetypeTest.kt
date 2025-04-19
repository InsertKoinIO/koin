package org.koin.core

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

class ScopeArchetypeTest {

    open class Archetype
    class ArchetypeExt : Archetype()
    class ClassA

    @KoinDslMarker
    fun Module.scopeArchetype(scopeSet: ScopeDSL.() -> Unit) {
        val qualifier = TypeQualifier(Archetype::class)
        ScopeDSL(qualifier, this).apply(scopeSet)
    }

    @OptIn(KoinInternalApi::class)
    @Test
    fun moduleArchetypeData() {
        val scopeModule = module {
            scopeArchetype {
                scoped { ClassA() }
            }
        }
        assertEquals(
            scopeModule.mappings.values.first().beanDefinition.scopeQualifier, TypeQualifier(Archetype::class)
        )
    }

    @Test
    fun declareAndRunArchetype() {
        val scopeModule = module {
            scopeArchetype {
                scoped { ClassA() }
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

        val a = scope.getOrNull<ClassA>()
        assertNotNull(a)
    }

    @Test
    fun declareAndRunArchetypeWithoutSource() {
        val scopeModule = module {
            scopeArchetype {
                scoped { ClassA() }
            }
        }
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(scopeModule)
        }.koin

        val archetypeExt = ArchetypeExt()
        val scope = koin.createScope<Archetype>(archetypeExt.getScopeId(), scopeArchetype = TypeQualifier(Archetype::class))

        val a = scope.getOrNull<ClassA>()
        assertNotNull(a)
    }
}
