package org.koin.core

import kotlin.test.fail
import kotlin.test.Test
import kotlin.test.*
import org.koin.KoinCoreTest
import org.koin.Simple
import org.koin.core.context.*
import org.koin.core.definition.Kind
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools
import org.koin.test.getBeanDefinition

class DynamicModulesTest : KoinCoreTest() {

    @Test
    fun `should unload single definition`() {
        val module = module {
            single { Simple.ComponentA() }
        }
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(module)
        }

        val defA = app.getBeanDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertEquals(Kind.Singleton, defA.kind)

        assertNotNull(app.koin.get<Simple.ComponentA>())

        app.unloadModules(module)

        assertNull(app.getBeanDefinition(Simple.ComponentA::class))

        try {
            app.koin.get<Simple.ComponentA>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should unload additional bound definition`() {
        val module = module {
            single { Simple.Component1() } bind Simple.ComponentInterface1::class
        }
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(module)
        }

        val defA = app.getBeanDefinition(Simple.Component1::class) ?: error("no definition found")
        assertEquals(Kind.Singleton, defA.kind)

        assertNotNull(app.koin.get<Simple.Component1>())
        assertNotNull(app.koin.get<Simple.ComponentInterface1>())

        app.unloadModules(module)

        assertNull(app.getBeanDefinition(Simple.ComponentA::class))

        try {
            app.koin.get<Simple.Component1>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }

        try {
            app.koin.get<Simple.ComponentInterface1>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should unload one module definition`() {
        val module1 = module {
            single { Simple.ComponentA() }
        }
        val module2 = module {
            single { Simple.ComponentB(get()) }
        }
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(listOf(module1, module2))
        }

        app.getBeanDefinition(Simple.ComponentA::class) ?: error("no definition found")
        app.getBeanDefinition(Simple.ComponentB::class) ?: error("no definition found")

        assertNotNull(app.koin.get<Simple.ComponentA>())
        assertNotNull(app.koin.get<Simple.ComponentB>())

        app.unloadModules(module2)

        assertNull(app.getBeanDefinition(Simple.ComponentB::class))

        try {
            app.koin.get<Simple.ComponentB>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should unload one module definition - factory`() {
        val module1 = module {
            single { Simple.ComponentA() }
        }
        val module2 = module {
            factory { Simple.ComponentB(get()) }
        }
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(listOf(module1, module2))
        }

        app.getBeanDefinition(Simple.ComponentA::class) ?: error("no definition found")
        app.getBeanDefinition(Simple.ComponentB::class) ?: error("no definition found")

        assertNotNull(app.koin.get<Simple.ComponentA>())
        assertNotNull(app.koin.get<Simple.ComponentB>())

        app.unloadModules(module2)

        assertNull(app.getBeanDefinition(Simple.ComponentB::class))

        try {
            app.koin.get<Simple.ComponentB>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should unload module override definition`() {
        val module1 = module {
            single { Simple.MySingle(42) }
        }
        val module2 = module {
            single { Simple.MySingle(24) }
        }
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(listOf(module1, module2))
        }

        app.getBeanDefinition(Simple.MySingle::class) ?: error("no definition found")
        assertEquals(24, app.koin.get<Simple.MySingle>().id)

        app.unloadModules(module2)

        assertNull(app.getBeanDefinition(Simple.MySingle::class))

        try {
            app.koin.get<Simple.MySingle>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should reload module definition`() {
        val module = module {
            single { (id: Int) -> Simple.MySingle(id) }
        }
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(module)
        }

        val koin = app.koin

        app.getBeanDefinition(Simple.MySingle::class) ?: error("no definition found")
        assertEquals(42, app.koin.get<Simple.MySingle> { parametersOf(42) }.id)

        koin.unloadModules(listOf(module))
        koin.loadModules(listOf(module))

        assertNotNull(app.getBeanDefinition(Simple.MySingle::class))

        assertEquals(24, app.koin.get<Simple.MySingle> { parametersOf(24) }.id)
    }

    @Test
    fun `create at start for external modules - definition`() {
        var created = false
        val module = module {
            single(createdAtStart = true) {
                created = true
                Simple.MySingle(42)
            }
        }
        val app = koinApplication {
            printLogger(Level.DEBUG)
        }

        val koin = app.koin

        koin.loadModules(listOf(module))

        assertTrue(created)
    }

    @Test
    fun `create at start for external modules`() {
        var created = false
        val module = module(createdAtStart = true) {
            single {
                created = true
                Simple.MySingle(42)
            }
        }
        val app = koinApplication {
            printLogger(Level.DEBUG)
        }

        val koin = app.koin

        koin.loadModules(listOf(module))

        assertTrue(created)
    }

    @Test
    fun `should reload module definition - global context`() {
        val module = module {
            single { (id: Int) -> Simple.MySingle(id) }
        }
        startKoin {
            printLogger(Level.DEBUG)
            modules(module)
        }

        assertEquals(
            42,
            KoinPlatformTools.defaultContext().get().get<Simple.MySingle> { parametersOf(42) }.id
        )

        unloadKoinModules(module)
        loadKoinModules(module)

        assertEquals(
            24,
            KoinPlatformTools.defaultContext().get().get<Simple.MySingle> { parametersOf(24) }.id
        )

        stopKoin()
    }

    @Test
    fun `should unload scoped definition`() {
        val scopeKey = named("-SCOPE-")
        val module = module {
            scope(scopeKey) {
                scoped { Simple.ComponentA() }
            }
        }
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(module)
        }

        val scope = app.koin.createScope("id", scopeKey)
        val defA = scope.getBeanDefinition(clazz = Simple.ComponentA::class)
            ?: error("no definition found")
        assertEquals(Kind.Scoped, defA.kind)
        assertEquals(scopeKey, defA.scopeQualifier)
        assertNotNull(scope.get<Simple.ComponentA>())

        app.unloadModules(module)

        assertNull(scope.getBeanDefinition(clazz = Simple.ComponentA::class))

        try {
            scope.get<Simple.ComponentA>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should reload scoped definition`() {
        val scopeKey = named("-SCOPE-")
        val module = module {
            scope(scopeKey) {
                scoped { Simple.ComponentA() }
            }
        }
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(module)
        }
        val koin = app.koin

        val scope = app.koin.createScope("id", scopeKey)
        val defA = scope.getBeanDefinition(clazz = Simple.ComponentA::class)
            ?: error("no definition found")

        assertEquals(Kind.Scoped, defA.kind)
        assertEquals(scopeKey, defA.scopeQualifier)
        assertNotNull(scope.get<Simple.ComponentA>())

        koin.unloadModules(listOf(module))
        koin.loadModules(listOf(module))

        scope.get<Simple.ComponentA>()
        assertNotNull(scope.getBeanDefinition(clazz = Simple.ComponentA::class))
    }

    @Test
    fun `should reload scoped definition - global`() {
        val scopeKey = named("-SCOPE-")
        val module = module {
            scope(scopeKey) {
                scoped { Simple.ComponentA() }
            }
        }
        startKoin {
            printLogger(Level.DEBUG)
            modules(module)
        }

        val scope = KoinPlatformTools.defaultContext().get().createScope("id", scopeKey)
        assertNotNull(scope.get<Simple.ComponentA>())

        unloadKoinModules(module)
        loadKoinModules(module)

        scope.get<Simple.ComponentA>()

        stopKoin()
    }
}