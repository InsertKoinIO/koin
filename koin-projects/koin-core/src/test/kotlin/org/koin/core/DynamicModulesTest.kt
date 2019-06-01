package org.koin.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
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
import org.koin.test.getDefinition

class DynamicModulesTest {

    @Test
    fun `should unload single definition`() {
        val module = module {
            single { Simple.ComponentA() }
        }
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(module)
        }

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        Assert.assertEquals(Kind.Single, defA.kind)

        Assert.assertNotNull(app.koin.get<Simple.ComponentA>())

        app.unloadModules(module)

        Assert.assertNull(app.getDefinition(Simple.ComponentA::class))

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

        val defA = app.getDefinition(Simple.Component1::class) ?: error("no definition found")
        Assert.assertEquals(Kind.Single, defA.kind)

        Assert.assertNotNull(app.koin.get<Simple.Component1>())
        Assert.assertNotNull(app.koin.get<Simple.ComponentInterface1>())

        app.unloadModules(module)

        Assert.assertNull(app.getDefinition(Simple.ComponentA::class))

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

        app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        app.getDefinition(Simple.ComponentB::class) ?: error("no definition found")

        Assert.assertNotNull(app.koin.get<Simple.ComponentA>())
        Assert.assertNotNull(app.koin.get<Simple.ComponentB>())

        app.unloadModules(module2)

        Assert.assertNull(app.getDefinition(Simple.ComponentB::class))

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

        app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        app.getDefinition(Simple.ComponentB::class) ?: error("no definition found")

        Assert.assertNotNull(app.koin.get<Simple.ComponentA>())
        Assert.assertNotNull(app.koin.get<Simple.ComponentB>())

        app.unloadModules(module2)

        Assert.assertNull(app.getDefinition(Simple.ComponentB::class))

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
        val module2 = module(override = true) {
            single { Simple.MySingle(24) }
        }
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(listOf(module1, module2))
        }

        app.getDefinition(Simple.MySingle::class) ?: error("no definition found")
        Assert.assertEquals(24, app.koin.get<Simple.MySingle>().id)

        app.unloadModules(module2)

        Assert.assertNull(app.getDefinition(Simple.MySingle::class))

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

        app.getDefinition(Simple.MySingle::class) ?: error("no definition found")
        Assert.assertEquals(42, app.koin.get<Simple.MySingle> { parametersOf(42) }.id)

        app.unloadModules(module)
        app.modules(module)
        Assert.assertNotNull(app.getDefinition(Simple.MySingle::class))

        Assert.assertEquals(24, app.koin.get<Simple.MySingle> { parametersOf(24) }.id)
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

        Assert.assertEquals(42, GlobalContext.get().koin.get<Simple.MySingle> { parametersOf(42) }.id)

        unloadKoinModules(module)
        loadKoinModules(module)

        Assert.assertEquals(24, GlobalContext.get().koin.get<Simple.MySingle> { parametersOf(24) }.id)

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
        val defA = scope.beanRegistry.findDefinition(clazz = Simple.ComponentA::class)
                ?: error("no definition found")
        Assert.assertEquals(Kind.Scoped, defA.kind)
        Assert.assertEquals(scopeKey, defA.scopeName)
        Assert.assertNotNull(scope.get<Simple.ComponentA>())

        app.unloadModules(module)

        Assert.assertNull(scope.beanRegistry.findDefinition(clazz = Simple.ComponentA::class))

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

        var scope = app.koin.createScope("id", scopeKey)
        val defA = scope.beanRegistry.findDefinition(clazz = Simple.ComponentA::class)
                ?: error("no definition found")
        Assert.assertEquals(Kind.Scoped, defA.kind)
        Assert.assertEquals(scopeKey, defA.scopeName)
        Assert.assertNotNull(scope.get<Simple.ComponentA>())

        app.unloadModules(module)
        app.modules(module)

        scope = app.koin.createScope("id", scopeKey)
        scope.get<Simple.ComponentA>()
        Assert.assertNotNull(scope.beanRegistry.findDefinition(clazz = Simple.ComponentA::class))
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

        var scope = GlobalContext.get().koin.createScope("id", scopeKey)
        Assert.assertNotNull(scope.get<Simple.ComponentA>())

        unloadKoinModules(module)
        loadKoinModules(module)

        scope = GlobalContext.get().koin.createScope("id", scopeKey)
        scope.get<Simple.ComponentA>()

        stopKoin()
    }
}