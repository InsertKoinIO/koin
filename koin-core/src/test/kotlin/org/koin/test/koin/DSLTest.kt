package org.koin.test.koin

import org.junit.Test
import org.koin.Koin
import org.koin.dsl.module.Module
import org.koin.test.ext.*


class ServiceA
class ServiceB
class ServiceC

class SimpleRootModule() : Module() {
    override fun context() = newContext {
        provide { ServiceA() }
    }
}

class SubContextModule() : Module() {
    override fun context() = newContext {
        provide { ServiceA() }

        scopeContext(ServiceB::class) {
            provide { ServiceB() }
        }
    }
}

class ComplexScopeModule() : Module() {
    override fun context() = newContext {

        scopeContext(ServiceB::class) {
            provide { ServiceB() }

            scopeContext(ServiceA::class) {
                provide { ServiceA() }
            }
        }

        scopeContext(ServiceC::class) {
            provide { ServiceC() }
        }
    }
}

class FullHierarchyModule() : Module() {
    override fun context() = newContext {
        scopeContext(ServiceC::class) {
            provide { ServiceC() }

            scopeContext(ServiceB::class) {
                provide { ServiceB() }

                scopeContext(ServiceA::class) {
                    provide { ServiceA() }
                }
            }
        }
    }
}


class DSLTest {

    @Test
    fun `can create a root context`() {
        val ctx = Koin().build(SimpleRootModule())

        ctx.assertScopes(1)
        ctx.assertDefinitions(1)
        ctx.assertInstances(0)
        ctx.assertRootScopeSize(0)
    }

    @Test
    fun `can create several contexts`() {
        val ctx = Koin().build(SubContextModule())

        ctx.assertScopes(2)
        ctx.assertDefinitions(2)
        ctx.assertInstances(0)
        ctx.assertRootScopeSize(0)

        val root = ctx.rootScope()
        val scopeB = ctx.getScope(ServiceB::class)
        ctx.assertScopeIsParent(root, scopeB)
    }

    @Test
    fun `can create more complex contexts`() {
        val ctx = Koin().build(ComplexScopeModule())

        ctx.assertScopes(4)
        ctx.assertDefinitions(3)
        ctx.assertInstances(0)
        ctx.assertRootScopeSize(0)

        val root = ctx.rootScope()
        val scopeB = ctx.getScope(ServiceB::class)
        val scopeC = ctx.getScope(ServiceC::class)
        val scopeA = ctx.getScope(ServiceA::class)

        ctx.assertScopeIsParent(root, scopeB)
        ctx.assertScopeIsParent(root, scopeC)
        ctx.assertScopeIsParent(scopeB, scopeA)
    }

    @Test
    fun `can create full hierarchy contexts`() {
        val ctx = Koin().build(FullHierarchyModule())

        ctx.assertScopes(4)
        ctx.assertDefinitions(3)
        ctx.assertInstances(0)
        ctx.assertRootScopeSize(0)

        val root = ctx.rootScope()
        val scopeB = ctx.getScope(ServiceB::class)
        val scopeC = ctx.getScope(ServiceC::class)
        val scopeA = ctx.getScope(ServiceA::class)

        ctx.assertScopeIsParent(root, scopeC)
        ctx.assertScopeIsParent(scopeC, scopeB)
        ctx.assertScopeIsParent(scopeB, scopeA)
    }
}