/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.koin.core

import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.NestedScope
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeCallback
import org.koin.core.scope.ScopeID
import org.koin.dsl.module

/**
 * Tests related to nested scopes.
 *
 * @author Andreas Schattney
 */
class NestedScopesTest {

    class Parent(val koin: Koin, scopeName: Qualifier = TypeQualifier(Parent::class)) {
        val currentScope: NestedScope by lazy {
            val scopeId = System.identityHashCode(this).toString()
            val scope = koin.createScope(scopeId, qualifier = scopeName)
            scope.declare(this)
            scope
        }
    }
    class Child(val koin: Koin, val parentScopeId: ScopeID, val qualifier: Qualifier = TypeQualifier(Child::class)) {
        val currentScope: NestedScope by lazy {
            val scopeId = System.identityHashCode(this).toString()
            val scope = koin.createScope(scopeId, qualifier, parentScopeId)
            scope.declare(this)
            scope
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    val module = module {

        factory(named("B")) { "Root" }

        scope(named<Parent>()) {

            factory(named("A")) {
                val root = get<String>(named("B"))
                "$root Parent"
            }

            nestedScope(named<Child>()) {
                scoped {
                    val parent = get<String>(named("A"))
                    "$parent Child"
                }
            }
        }
    }

    @Test
    fun `can resolve bean definitions from same scope`() {
        val koin = startKoin {
            modules(module)
        }.koin

        val parentScope = Parent(koin).currentScope
        val child = Child(koin, parentScope.id)

        val directResolution = child.currentScope.get<String>()
        assertEquals("Root Parent Child" , directResolution)
    }

    @Test
    fun `can resolve bean definitions from parent scope`() {
        val koin = startKoin {
            modules(module)
        }.koin

        val parentScope = Parent(koin).currentScope
        val child = Child(koin, parentScope.id)

        val directResolution = child.currentScope.get<String>(named("A"))
        assertEquals("Root Parent" , directResolution)
    }

    @Test
    fun `can resolve bean definitions from root scope`() {
        val koin = startKoin {
            modules(module)
        }.koin

        val parentScope = Parent(koin).currentScope
        val child = Child(koin, parentScope.id)

        val directResolution = child.currentScope.get<String>(named("B"))
        assertEquals("Root" , directResolution)
    }

    @Test
    fun `direct resolution not possible when parent scope already closed`() {
        val koin = startKoin {
            modules(module)
        }.koin

        val parentScope = Parent(koin).currentScope
        val child = Child(koin, parentScope.id)
        parentScope.close()

        try {
            child.currentScope.get<String>()
            fail()
        } catch (e: Exception) {

        }
    }

    @Test
    fun `parenting possible across multiple modules`() {
        val parentModule = module {
            factory(named("B")) { "Root" }
            scope(named<Parent>()) {
                factory(named("A")) {
                    val root = get<String>(named("B"))
                    "$root Parent"
                }
            }
        }
        val childModule = module {
            scope(named<Child>()) {
                scoped {
                    val parent = this.get<String>(named("A"))
                    "$parent Child"
                }
            }
        }
        val koin = startKoin {
            modules(listOf(parentModule, childModule))
        }.koin

        val parentScope = Parent(koin).currentScope
        val child = Child(koin, parentScope.id)

        val directResolution = child.currentScope.get<String>()
        assertEquals("Root Parent Child" , directResolution)
    }

    @Test
    fun `can provision actual instance from parent to child`() {
        val parentQualifier = named("PARENT")

        val koin = startKoin {
            modules(listOf(module {

                scope(parentQualifier) {

                    nestedScope(named<Child>()) {
                        scoped<Pair<Child, Parent>> {
                            get<Child>() to get<Parent>()
                        }
                    }
                }
            }))
        }.koin

        val parent = Parent(koin, parentQualifier)
        val child = Child(koin, parent.currentScope.id)

        val (directInstance, parentInstance) = child.currentScope.get<Pair<Child, Parent>>()

        assertTrue(parent == parentInstance)
        assertTrue(child == directInstance)
    }

    @Test
    fun `removes definitions of child scopes when module is unloaded`() {
        val module = module {
            single { "A" }
            scope(named<Parent>()) {
                scoped(named("Parent")) { "Parent" }
                nestedScope(named<Child>()) {
                    scoped(named("Child")) { "Child" }
                }
            }
        }
        val app = startKoin {
            modules(module)
        }

        val koin = app.koin
        val definitionsBeforeUnload = koin.scopeRegistry.definitions
        assertEquals(2, definitionsBeforeUnload.size)
        definitionsBeforeUnload.values.forEach {
            assertEquals(1, it.definitions.size)
        }

        app.unloadModules(module)

        val definitionsAfterUnload = koin.scopeRegistry.definitions
        assertEquals(2, definitionsAfterUnload.size)
        definitionsAfterUnload.values.forEach {
            assertEquals(0, it.definitions.size)
        }
    }

    @Test
    fun `closes child scopes when parent scope is closed`() {
        val module = module {
            scope(named<Parent>()) {
                nestedScope(named<Child>()) {
                    nestedScope(named("A"))
                }
            }
        }
        val app = startKoin {
            modules(module)
        }

        val koin = app.koin
        val parent = Parent(koin).currentScope
        val child = Child(koin, parent.id).currentScope
        val childA = Child(koin, child.id, named("A")).currentScope

        var calls = 0
        val scopes = listOf<Scope>(parent, child, childA)
        (scopes + koin.rootScope).forEach {
            it.registerCallback(object: ScopeCallback {
                override fun onScopeClose(scope: Scope) {
                    calls++
                }
            })
        }

        parent.close()

        assertEquals(scopes.size, calls)
        scopes.forEach { scope ->
            assertNull(koin.scopeRegistry.getScopeInstanceOrNull(scope.id))
        }

    }

    @Test
    fun `root scope is assigned as parent scope when no scope id is defined for parent`() {
        val module = module {
            scope(named<Parent>()) { }
        }
        val app = startKoin {
            modules(module)
        }

        val koin = app.koin
        val scope = Parent(koin).currentScope

        assertTrue(koin.rootScope === scope.parentScope)
    }

    @Test
    fun `can resolve dependencies from parent scope of same type and without qualifier`() {
        abstract class Service {
            abstract val value: String
        }
        class Base: Service() {
            override val value: String = "Base"
        }
        class ServiceA(parent: Service): Service() {
            override val value: String = "${parent.value} A"
        }
        class ServiceB(parent: Service): Service() {
            override val value: String = "${parent.value} B"
        }
        val module = module {
            scope(named("A")) {
                scoped<Service> { Base() }
            }
            scope(named("B")) {
                scoped<Service> { ServiceA(parentScope.get()) }
            }
            scope(named("C")) {
                scoped<Service> { ServiceB(parentScope.get()) }
            }
        }
        val app = startKoin {
            modules(module)
        }
        val koin = app.koin

        val scopeA = koin.createScope("id1", named("A"))
        val scopeB = koin.createScope("id2", named("B"), "id1")
        val scopeC = koin.createScope("id3", named("C"), "id2")

        val service = scopeC.get<Service>()
        assertEquals("Base A B", service.value)
        assertEquals("Base A", scopeB.get<Service>().value)
        assertEquals("Base", scopeA.get<Service>().value)
    }
}