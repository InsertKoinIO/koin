package org.koin.test.android

import android.arch.lifecycle.LifecycleOwner
import org.junit.Assert.*
import org.junit.Test
import org.koin.android.scope.currentScope
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.android.util.Child
import org.koin.test.android.util.Parent

class ChildScopesTest: AutoCloseKoinTest() {

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
        startKoin {
            modules(module)
        }.koin

        val parent = Parent()
        val child = Child(parent.currentScope.id)

        val directResolution = child.currentScope.get<String>()
        assertEquals("Root Parent Child" , directResolution)
    }

    @Test
    fun `can resolve bean definitions from parent scope`() {
        startKoin {
            modules(module)
        }.koin

        val parent = Parent()
        val child = Child(parent.currentScope.id)

        val directResolution = child.currentScope.get<String>(named("A"))
        assertEquals("Root Parent" , directResolution)
    }

    @Test
    fun `can resolve bean definitions from root scope`() {
        startKoin {
            modules(module)
        }.koin

        val parent = Parent()
        val child = Child(parent.currentScope.id)

        val directResolution = child.currentScope.get<String>(named("B"))
        assertEquals("Root" , directResolution)
    }

    @Test
    fun `direct resolution not possible when parent scope already closed`() {
        startKoin {
            modules(module)
        }.koin

        val parent = Parent()
        val child = Child(parent.currentScope.id)
        parent.currentScope.close()

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
        startKoin {
            modules(listOf(parentModule, childModule))
        }

        val parent = Parent()
        val child = Child(parent.currentScope.id)

        val directResolution = child.currentScope.get<String>()
        assertEquals("Root Parent Child" , directResolution)
    }

    @Test
    fun `can provision actual instance from parent to child`() {
        val parentQualifier = named("PARENT")

        val koin = startKoin {
            modules(listOf(module {

                objectScope<LifecycleOwner>(parentQualifier) {

                    scoped(named("TEST")) { instance }

                    childObjectScope<Child> {
                        scoped<Pair<LifecycleOwner, LifecycleOwner>> {
                            instance to get(named("TEST"))
                        }
                    }
                }
            }))
        }.koin

        val parent = Parent()
        val parentScope = koin.createObjectScoped(parent, scopeName = parentQualifier)
        val child = Child(parentScope.id)

        val (directInstance, parentInstance) = child.currentScope.get<Pair<LifecycleOwner, LifecycleOwner>>()

        assertTrue(parent == parentInstance)
        assertTrue(child == directInstance)
    }

}