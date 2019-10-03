package org.koin.core

import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.HasParentScope
import org.koin.core.scope.ScopeID
import org.koin.dsl.module

class ChildScopesTest {

    class Parent(val koin: Koin, scopeName: Qualifier = TypeQualifier(Parent::class)) {
        val currentScope = koin.createObjectScoped(this, scopeName = scopeName)
    }
    class Child(val koin: Koin, override val parentScopeId: ScopeID): HasParentScope {
        val currentScope = koin.createObjectScoped(this, parentScopeId = parentScopeId)
    }

    @After
    fun after() {
        stopKoin()
    }

    val module = module {

        factory(named("B")) { "Root" }

        objectScope<Parent> {

            factory(named("A")) {
                val root = get<String>(named("B"))
                "$root Parent"
            }

            childObjectScope<Child> {
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
            objectScope<Parent> {
                factory(named("A")) {
                    val root = get<String>(named("B"))
                    "$root Parent"
                }
            }
        }
        val childModule = module {
            objectScope<Child> {
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

                objectScope<Parent>(parentQualifier) {

                    scoped(named("TEST")) { instance }

                    childObjectScope<Child> {
                        scoped<Pair<Child, Parent>> {
                            instance to get(named("TEST"))
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

}