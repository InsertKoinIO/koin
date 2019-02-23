package org.koin.test

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.core.instance.InstanceContext
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.check.SandboxInstance
import org.koin.test.check.sandboxed

class SandBoxInstanceTest {

    @Test
    fun `Create a sandbox for given definition`() {
        val koin = koinApplication {
            defaultLogger(Level.DEBUG)
            modules(
                    module {
                        single { Simple.ComponentA() }
                    }
            )
        }.koin

        val def = koin.beanRegistry.findDefinition(clazz = Simple.ComponentA::class)!!
        val instance = def.instance?.get<Simple.ComponentA>(InstanceContext(koin = koin))

        val sandboxedDef = def.sandboxed()
        sandboxedDef.createInstanceHolder()
        val sandboxedInstance = sandboxedDef.instance
        assertTrue(sandboxedInstance is SandboxInstance)

        val mock = sandboxedInstance?.get<Simple.ComponentA>(InstanceContext(koin = koin))

        assertNotEquals(instance, mock)
    }
}