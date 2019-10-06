package org.koin.core

import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.HasParentScope
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeCallback
import org.koin.core.scope.ScopeID
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ChildScopesTest {

    class Parent(val koin: Koin, scopeName: Qualifier = TypeQualifier(Parent::class)) {
        val currentScope = koin.createObjectScoped(this, scopeName = scopeName)
    }
    class Child(val koin: Koin, override val parentScopeId: ScopeID, val qualifier: Qualifier = TypeQualifier(Child::class)): HasParentScope {
        val currentScope = koin.createObjectScoped(this, scopeName = qualifier, parentScopeId = parentScopeId)
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

    @Test
    fun `removes definitions of child scopes when module is unloaded`() {
        val module = module {
            objectScope<Parent> {
                childObjectScope<Child>()
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
            objectScope<Parent> {
                childObjectScope<Child> {
                    childObjectScope<Child>(named("A"))
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
            objectScope<Parent>()
        }
        val app = startKoin {
            modules(module)
        }

        val koin = app.koin
        val scope = Parent(koin).currentScope

        assertTrue(koin.rootScope === scope.parentScope)
    }

    @Test
    fun `can override beandefinition in RootScope by loading a module`() {
        val module = module {
            single { "A" }
        }
        val app = startKoin {
            modules(module)
        }

        val koin = app.koin
        loadKoinModules(module {
            single(override = true) { "B" }
        })

        assertEquals("B", koin.get<String>())
    }

    @Test
    fun `detects duplicate bean definitions in identical ScopeSets across modules`() {
        val moduleA = module {
            objectScope<Parent> {
                scoped { "A" }
            }
        }
        val moduleB = module {
            objectScope<Parent> {
                scoped { "A" }
            }
        }
        try {
            koinApplication {
                modules(listOf(moduleA, moduleB))
            }
            fail("Should have thrown ${DefinitionOverrideException::class.java.simpleName}")
        } catch (e: DefinitionOverrideException) {

        }

        startKoin {
            modules(listOf(moduleA))
        }

        try {
            loadKoinModules(moduleB)
            fail("Should have thrown ${DefinitionOverrideException::class.java.simpleName}")
        } catch (e: DefinitionOverrideException) {

        }

    }

    @Test
    fun `can override bean definition in ScopeSet by loading a module`() {
        val module = module {
            objectScope<Parent> {
                scoped { "A" }
            }
        }
        val app = startKoin {
            modules(module)
        }

        val koin = app.koin
        loadKoinModules(module {
            objectScope<Parent> {
                scoped(override = true) {"B"}
            }
        })

        val scope = Parent(koin).currentScope
        assertEquals("B", scope.get<String>())
    }

    @Test
    fun `can override bean definition in ScopeSet more than once`() {
        val module = module {
            objectScope<Parent> {
                scoped { "A" }
            }
        }
        val app = startKoin {
            modules(module)
        }

        val koin = app.koin
        loadKoinModules(module {
            objectScope<Parent> {
                scoped(override = true) {"B"}
            }
        })
        loadKoinModules(module {
            objectScope<Parent> {
                scoped(override = true) {"C"}
            }
        })

        val scope = Parent(koin).currentScope
        assertEquals("C", scope.get<String>())
    }
}