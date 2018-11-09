package org.koin.test

import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.check.cloneForSandbox

class SandBoxInstanceTest {

    @Test
    fun `Create a sandbox for given definition`() {
        val koin = koinApplication {
            useLogger(Level.DEBUG)
            loadModules(
                module {
                    single { Simple.ComponentA() }
                }
            )
        }.koin

        val def = koin.beanRegistry.findDefinition(clazz = Simple.ComponentA::class)!!
        val instance = def.instance.get<Simple.ComponentA>(null, null)

        val sandboxedDef = def.cloneForSandbox()
        val mock = sandboxedDef.instance.get<Simple.ComponentA>(null, null)

        assertNotEquals(instance, mock)
    }
}