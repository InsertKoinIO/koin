package org.koin.dsl

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.Simple
import org.koin.test.getDefinition

class ModuleOptions {

    @Test
    fun `module default options`() {
        val module = module {
        }

        assertFalse(module.isCreatedAtStart)
        assertFalse(module.override)
    }

    @Test
    fun `module override option`() {
        val module = module(override = true) {
        }

        assertFalse(module.isCreatedAtStart)
        assertTrue(module.override)
    }

    @Test
    fun `module created options`() {
        val module = module(createdAtStart = true) {
        }

        assertTrue(module.isCreatedAtStart)
        assertFalse(module.override)
    }

    @Test
    fun `module definitions options inheritance`() {

        val module = module(createdAtStart = true, override = true) {
            single { Simple.ComponentA() }
        }

        val app = koinApplication {
            modules(module)
        }

        assertTrue(module.isCreatedAtStart)
        assertTrue(module.override)

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertTrue(defA.options.isCreatedAtStart)
        assertTrue(defA.options.override)
    }

    @Test
    fun `module definitions options non inheritance`() {

        val module = module {
            single(createdAtStart = true) { Simple.ComponentA() }
            single(override = true) { Simple.ComponentB(get()) }
        }

        val app = koinApplication {
            modules(module)
        }

        assertFalse(module.isCreatedAtStart)
        assertFalse(module.override)

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertTrue(defA.options.isCreatedAtStart)
        assertFalse(defA.options.override)

        val defB = app.getDefinition(Simple.ComponentB::class) ?: error("no definition found")
        assertFalse(defB.options.isCreatedAtStart)
        assertTrue(defB.options.override)
    }
}