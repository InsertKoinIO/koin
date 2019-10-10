package org.koin.core

import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.error.NoScopeDefinitionFoundException
import org.koin.core.error.ScopeAlreadyCreatedException
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeCallback
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ScopeAPITest {

    @After
    fun after() {
        stopKoin()
    }

    val scopeKey = named("KEY")
    val koin = koinApplication {
        modules(
                module {
                    scope(scopeKey) {
                    }
                }
        )
    }.koin

    @Test
    fun `create a scope instance`() {
        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId, scopeKey)
        val scope2 = koin.getScope(scopeId)

        assertEquals(scope1, scope2)
    }

    @Test
    fun `can't find a non created scope instance`() {
        val scopeId = "myScope"
        try {
            koin.getScope(scopeId)
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `create different scopes`() {
        val scope1 = koin.createScope("myScope1", scopeKey)
        val scope2 = koin.createScope("myScope2", scopeKey)

        assertNotEquals(scope1, scope2)
    }

    @Test
    fun `can't create scope instance with unknown scope def`() {

        try {
            koin.createScope("myScope", named("a_scope"))
            fail()
        } catch (e: NoScopeDefinitionFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `create scope instance with scope def`() {

        assertNotNull(koin.createScope("myScope", scopeKey))
    }

    @Test
    fun `can't create a new scope if not closed`() {
        koin.createScope("myScope1", scopeKey)
        try {
            koin.createScope("myScope1", scopeKey)
            fail()
        } catch (e: ScopeAlreadyCreatedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `can't get a closed scope`() {

        val scope = koin.createScope("myScope1", scopeKey)
        scope.close()
        try {
            koin.getScope("myScope1")
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `find a scope by id`() {
        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId, scopeKey)
        assertEquals(scope1, koin.getScope(scope1.id))
    }

    @Test
    fun `scope callback`() {
        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId, scopeKey)
        var closed = false
        scope1.registerCallback(object : ScopeCallback {
            override fun onScopeClose(scope: Scope) {
                closed = true
            }
        })
        scope1.close()
        assertTrue(closed)
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
            scope(named("A")) {
                scoped { "A" }
            }
        }
        val moduleB = module {
            scope(named("A")) {
                scoped { "A" }
            }
        }
        try {
            startKoin {
                modules(listOf(moduleA, moduleB))
            }
            fail("Should have thrown ${DefinitionOverrideException::class.java.simpleName}")
        } catch (e: DefinitionOverrideException) {

        }

        koinApplication {
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
            scope(named("A")) {
                scoped { "A" }
            }
        }
        val app = startKoin {
            modules(module)
        }

        val koin = app.koin
        loadKoinModules(module {
            scope(named("A")) {
                scoped(override = true) {"B"}
            }
        })

        val scope = koin.createScope("123", named("A"))
        assertEquals("B", scope.get<String>())
    }

    @Test
    fun `can override bean definition in ScopeSet more than once`() {
        val module = module {
            scope(named("A")) {
                scoped { "A" }
            }
        }
        val app = startKoin {
            modules(module)
        }

        val koin = app.koin
        loadKoinModules(module {
            scope(named("A")) {
                scoped(override = true) {"B"}
            }
        })
        loadKoinModules(module {
            scope(named("A")) {
                scoped(override = true) {"C"}
            }
        })

        val scope = koin.createScope("123", named("A"))
        assertEquals("C", scope.get<String>())
    }
}
