package org.koin.test.koin

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.dsl.module.Module
import org.koin.test.ext.*


class ServiceA
class ServiceB
class ServiceC
class ServiceD

class SimpleRootModule() : Module() {
    override fun context() = newContext {
        provide { ServiceA() }
    }
}

class SubContextModule() : Module() {
    override fun context() = newContext {
        provide { ServiceA() }

        subContext(ServiceB::class) {
            provide { ServiceB() }
        }
    }
}

class ComplexScopeModule() : Module() {
    override fun context() = newContext {

        subContext(ServiceB::class) {
            provide { ServiceB() }

            subContext(ServiceA::class) {
                provide { ServiceA() }
            }
        }

        subContext(ServiceC::class) {
            provide { ServiceC() }
        }
    }
}

class FullHierarchyModule() : Module() {
    override fun context() = newContext {

        provide { ServiceD() }

        subContext(ServiceC::class) {
            provide { ServiceC() }

            subContext(ServiceB::class) {
                provide { ServiceB() }

                subContext(ServiceA::class) {
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
        ctx.assertRootScope(0)
    }

    @Test
    fun `can create several contexts`() {
        val ctx = Koin().build(SubContextModule())

        ctx.assertScopes(2)
        ctx.assertDefinitions(2)
        ctx.assertInstances(0)
        ctx.assertRootScope(0)

        val root = ctx.rootScope()
        val scopeB = ctx.getScope(ServiceB::class)
        ctx.assertParentScope(root, scopeB)
    }

    @Test
    fun `complex context visibility`() {
        val ctx = Koin().build(SubContextModule())

        val root = ctx.rootScope()
        val scopeB = ctx.getScope(ServiceB::class)
        ctx.assertParentScope(root, scopeB)
        ctx.assertVisible(root, ServiceB::class)
    }

    @Test
    fun `can create more complex contexts`() {
        val ctx = Koin().build(ComplexScopeModule())

        ctx.assertScopes(4)
        ctx.assertDefinitions(3)
        ctx.assertInstances(0)
        ctx.assertRootScope(0)

        val root = ctx.rootScope()
        val scopeB = ctx.getScope(ServiceB::class)
        val scopeC = ctx.getScope(ServiceC::class)
        val scopeA = ctx.getScope(ServiceA::class)

        ctx.assertParentScope(root, scopeB)
        ctx.assertParentScope(root, scopeC)
        ctx.assertParentScope(scopeB, scopeA)
    }

    @Test
    fun `more complex context visibility`() {
        val ctx = Koin().build(ComplexScopeModule())

        val root = ctx.rootScope()
        val scopeB = ctx.getScope(ServiceB::class)
        val scopeC = ctx.getScope(ServiceC::class)
        val scopeA = ctx.getScope(ServiceA::class)

        ctx.assertParentScope(root, scopeB)
        ctx.assertParentScope(root, scopeC)
        ctx.assertParentScope(scopeB, scopeA)

        ctx.assertVisible(root, ServiceB::class)
        ctx.assertVisible(root, ServiceA::class)
        ctx.assertVisible(root, ServiceC::class)

        ctx.assertVisible(scopeA, ServiceA::class)
        ctx.assertVisible(scopeB, ServiceA::class)
        ctx.assertNotVisible(scopeC, ServiceA::class)

        ctx.assertNotVisible(scopeA, ServiceB::class)
        ctx.assertVisible(scopeB, ServiceB::class)
        ctx.assertNotVisible(scopeC, ServiceB::class)

        ctx.assertNotVisible(scopeA, ServiceC::class)
        ctx.assertNotVisible(scopeB, ServiceC::class)
        ctx.assertVisible(scopeB, ServiceC::class)
    }

    @Test
    fun `can create full hierarchy contexts`() {
        val ctx = Koin().build(FullHierarchyModule())

        ctx.assertScopes(4)
        ctx.assertDefinitions(3)
        ctx.assertInstances(0)
        ctx.assertRootScope(0)

        val root = ctx.rootScope()
        val scopeB = ctx.getScope(ServiceB::class)
        val scopeC = ctx.getScope(ServiceC::class)
        val scopeA = ctx.getScope(ServiceA::class)

        ctx.assertParentScope(root, scopeC)
        ctx.assertParentScope(scopeC, scopeB)
        ctx.assertParentScope(scopeB, scopeA)
    }

    @Test
    fun `full hierarchy context visibility`() {
        val ctx = Koin().build(FullHierarchyModule())

        val root = ctx.rootScope()
        val scopeA = ctx.getScope(ServiceA::class)
        val scopeB = ctx.getScope(ServiceB::class)
        val scopeC = ctx.getScope(ServiceC::class)

        Assert.assertEquals(root, ctx.beanRegistry.scope(ctx.definition(ServiceD::class)))

        ctx.assertParentScope(root, scopeC)
        ctx.assertParentScope(scopeC, scopeB)
        ctx.assertParentScope(scopeB, scopeA)

        ctx.assertVisible(root, ServiceA::class)
        ctx.assertVisible(scopeC, ServiceA::class)
        ctx.assertVisible(scopeB, ServiceA::class)
        ctx.assertVisible(scopeA, ServiceA::class)

        ctx.assertVisible(root, ServiceB::class)
        ctx.assertVisible(scopeC, ServiceB::class)
        ctx.assertVisible(scopeB, ServiceB::class)
        ctx.assertNotVisible(scopeA, ServiceB::class)

        ctx.assertVisible(root, ServiceC::class)
        ctx.assertVisible(scopeC, ServiceC::class)
        ctx.assertNotVisible(scopeB, ServiceC::class)
        ctx.assertNotVisible(scopeA, ServiceC::class)

        ctx.assertVisible(root, ServiceD::class)
        ctx.assertNotVisible(scopeA, ServiceD::class)
        ctx.assertNotVisible(scopeB, ServiceD::class)
        ctx.assertNotVisible(scopeA, ServiceD::class)
    }
}