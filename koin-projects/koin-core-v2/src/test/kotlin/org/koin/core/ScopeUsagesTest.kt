package org.koin.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.Simple
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.scope.Scope
import org.koin.dsl.SCOPE_ID
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.dsl.withScope
import org.koin.test.assertScopeHasBeenCreated
import org.koin.test.getDefinition

class ScopeUsagesTest {

    @Test
    fun `create a scope`() {
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID) { Simple.ComponentA() }
            })
        }
        val koin = app.koin

        val scope: Scope = koin.createScope(SCOPE_ID)
        assertEquals(SCOPE_ID, scope.id)

        app.assertScopeHasBeenCreated(scope)
    }

    @Test
    fun `can't resolve a component from a non created scope`() {
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID) { Simple.ComponentA() }
            })
        }
        val koin = app.koin

        try {
            koin.get<Simple.ComponentA>()
            fail("Should not be able to resolve a scoped component without creating its scope")
        } catch (e: ScopeNotCreatedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `can't create an already created scope`() {
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID) { Simple.ComponentA() }
            })
        }
        val koin = app.koin
        koin.createScope(SCOPE_ID)
        try {
            koin.createScope(SCOPE_ID)
            fail("should not recreate a scope")
        } catch (e: ScopeAlreadyCreatedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `can create & get an already created scope`() {
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID) { Simple.ComponentA() }
            })
        }
        val koin = app.koin
        val c1 = koin.createScope(SCOPE_ID)
        val c2 = koin.getOrCreateScope(SCOPE_ID)
        assertEquals(c1, c2)
    }

    @Test
    fun `can create close recreate a scope`() {
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID) { Simple.ComponentA() }
            })
        }
        val koin = app.koin
        val c1 = koin.createScope(SCOPE_ID)
        val a1 = koin.get<Simple.ComponentA>()
        c1.close()
        val c2 = koin.createScope(SCOPE_ID)
        val a2 = koin.get<Simple.ComponentA>()

        assertNotEquals(c1,c2)
        assertNotEquals(a1,a2)
    }

    @Test
    fun `created scope is associated to Koin instance`() {
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID) { Simple.ComponentA() }
            })
        }
        val koin = app.koin
        val c1 = koin.createScope(SCOPE_ID)
        c1.close()
    }

    @Test
    fun `resolve a component from a created scope`() {
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID) { Simple.ComponentA() }
            })
        }
        val koin = app.koin
        koin.createScope(SCOPE_ID)
        assertNotNull(koin.get<Simple.ComponentA>())
    }

    @Test
    fun `close a scope with resolved component`() {
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID) { Simple.ComponentA() }
            })
        }
        val koin = app.koin
        val scope = koin.createScope(SCOPE_ID)

        koin.get<Simple.ComponentA>()

        scope.close()
        val def = app.getDefinition(Simple.ComponentA::class)!!
        assertTrue(!def.instance.isCreated())
    }

    @Test
    fun `close a scope with resolved component by name`() {
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID, name = "default") { Simple.ComponentA() }
            })
        }
        val koin = app.koin
        val scope = koin.createScope(SCOPE_ID)

        koin.get<Simple.ComponentA>("default")

        scope.close()
        val def = app.getDefinition(Simple.ComponentA::class)!!
        assertTrue(!def.instance.isCreated())
    }

    @Test
    fun `close a group scope components`() {
        val app = koinApplication {
            loadModules(module {
                withScope(SCOPE_ID) {
                    scoped { Simple.ComponentA() }
                    scoped { Simple.ComponentB(get()) }
                }
            })
        }
        val koin = app.koin
        val scope = koin.createScope(SCOPE_ID)

        koin.get<Simple.ComponentA>()
        koin.get<Simple.ComponentB>()

        scope.close()
        val defA = app.getDefinition(Simple.ComponentA::class)!!
        assertTrue(!defA.instance.isCreated())
        val defB = app.getDefinition(Simple.ComponentB::class)!!
        assertTrue(!defB.instance.isCreated())
    }

    @Test
    fun `close a scope between several`() {
        val SCOPE2_ID = "SCOPE2"
        val app = koinApplication {
            loadModules(module {
                scope(SCOPE_ID) { Simple.ComponentA() }
                scope(SCOPE2_ID) { Simple.ComponentB(get()) }
            })
        }
        val koin = app.koin
        val scope1 = koin.createScope(SCOPE_ID)
        val scope2 = koin.createScope(SCOPE2_ID)

        koin.get<Simple.ComponentA>()
        koin.get<Simple.ComponentB>()

        scope2.close()

        val defA = app.getDefinition(Simple.ComponentA::class)!!
        assertTrue(defA.instance.isCreated(scope1))

        val defB = app.getDefinition(Simple.ComponentB::class)!!
        assertTrue(!defB.instance.isCreated(scope2))
    }
}