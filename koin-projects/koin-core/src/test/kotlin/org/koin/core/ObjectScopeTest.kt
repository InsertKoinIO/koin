package org.koin.core

import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.ScopeID
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ObjectScopeTest {

    @After
    fun after() {
        stopKoin()
    }

    class SomeServiceProvider
    class SomeScopedService(val obj: SomeServiceProvider)
    class Parent(val koin: Koin) {
        val currentScope = koin.createObjectScoped(this)
    }
    class Child(val koin: Koin, parentScopeId: ScopeID? = null) {
        val currentScope = koin.createObjectScoped(this, parentScopeId = parentScopeId)
    }
    class Service

    val module = module {
        objectScope<SomeServiceProvider> {
            scoped { SomeScopedService(instance) }
        }
        objectScope<Parent> {
            scoped { Service() }
            childScope(named<Child>())
        }
        scope(named<Child>()) {
            factory { "ABC" }
        }
    }

    @Test
    fun `bean definition can access instance from scope`() {
        val koin = koinApplication {
            modules(module)
        }.koin

        val owner = SomeServiceProvider()
        val scope = koin.createObjectScoped(owner)
        val someObject = scope.get<SomeScopedService>()

        assertTrue(owner == someObject.obj)
    }

    @Test
    fun `objects not resolvable after scope is closed`() {
        val koin = startKoin {
            modules(module)
        }.koin

        val owner = Parent(koin)
        val scope = owner.currentScope
        scope.get<Service>()
        scope.close()

        try {
            scope.get<Service>()
            fail("no resolution of closed scope dependency")
        } catch (e: Throwable) {
            assertEquals(0, scope.beanRegistry.size())
        }
    }

    @Test
    fun `scope with instances can be used to access bean definitions within default scope declarations`() {
        val koin = startKoin {
            modules(module)
        }.koin

        val parentScopeId = Parent(koin).currentScope.id
        val owner = Child(koin, parentScopeId)
        val scope = owner.currentScope
        assertEquals("ABC", scope.get<String>())
    }


    @Test
    fun `parent instance is available at child scope by default`() {
        val koin = startKoin {
            modules(module)
        }.koin

        val parent = Parent(koin)
        val child = Child(koin, parentScopeId = parent.currentScope.id)
        val childScope = child.currentScope

        assertEquals(parent, childScope.get<Parent>())
    }

    open class Abstraction
    class Implementation: Abstraction()

    @Test
    fun `does create default instance`() {
        val koin = startKoin {
            modules(module {
                objectScope<Implementation>()
            })
        }.koin

        val scope = koin.createObjectScoped(Implementation())

        assertEquals(1, scope.scopeDefinition?.definitions?.size)
    }

    @Test
    fun `does not create default instance provision declaration when primary type already present in definitions`() {
        val koin = startKoin {
            modules(module {
                objectScope<Implementation> {
                    scoped { instance }
                }
            })
        }.koin

        val scope = koin.createObjectScoped(Implementation())

        assertEquals(1, scope.scopeDefinition?.definitions?.size)
    }

    @Test
    fun `scope qualifier is based on the generic type instead of implementation type at scope creation`() {
        val koin = startKoin {
            modules(module {
                objectScope<Abstraction>()
            })
        }.koin

        val instance = Implementation()
        val scope = koin.createObjectScoped<Abstraction>(instance)
        assertEquals(1, scope.scopeDefinition?.definitions?.size)
        assertEquals(instance, scope.get<Abstraction>())
    }
}