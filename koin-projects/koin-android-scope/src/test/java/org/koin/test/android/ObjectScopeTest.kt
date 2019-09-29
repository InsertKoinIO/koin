package org.koin.test.android

import android.arch.lifecycle.Lifecycle
import org.junit.Assert.*
import org.junit.Test
import org.koin.android.scope.currentScope
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.getScopeId
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.android.util.AChild
import org.koin.test.android.util.AParent

class ObjectScopeTest: AutoCloseKoinTest() {

    class SomeServiceProvider
    class SomeScopedService(val obj: SomeServiceProvider)

    class Child

    val module = module {
        objectScope<SomeServiceProvider> {
            scoped { SomeScopedService(instance) }
        }
        objectScope<AParent> {
            scoped { AChild(instance.currentScope.id) }
            childScope(named<Child>())
        }
        scope(named<AChild>()) {
            factory { "ABC" }
        }
    }

    @Test
    fun `bean definition can access instance from scope`() {
        val koin = startKoin {
            modules(module)
        }.koin

        val owner = SomeServiceProvider()
        val scope = koin.createObjectScoped(owner)
        val someObject = scope.get<SomeScopedService>()

        assertTrue(owner == someObject.obj)
    }

    @Test
    fun `objects not resolvable after state marked as destroyed`() {
        startKoin {
            modules(module)
        }.koin

        val owner = AParent()
        val scope = owner.currentScope
        owner.markState(Lifecycle.State.DESTROYED)

        try {
            scope.get<AChild>()
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

        val owner = AChild(koin.rootScope.id)
        val scope = owner.currentScope
        assertEquals("ABC", scope.get<String>())
    }


    @Test
    fun `parent instance is available at child scope by default`() {
        val koin = startKoin {
            modules(module)
        }.koin

        val parent = AParent()
        val child = Child()
        val parentScope = koin.createObjectScoped(parent)
        val childScope = koin.createScope(child.getScopeId(), named<Child>(), parentId = parentScope.id)

        val instance = childScope.get<AParent>()
        assertEquals(parentScope.instance, instance)
    }
}