package org.koin.dsl

import org.junit.Test
import org.koin.test.assertDefinitionsCount

class ModuleCreationTest {

    @Test
    fun `create an empty module`() {
        val app = koin {
            loadModules(module {})
        }

        app.assertDefinitionsCount(0)
    }

    @Test
    fun `load a module once started`() {
        val app = koin {}

        app.assertDefinitionsCount(0)

        app.loadModules(module {
            single { Simple.ComponentA() }
        })
        app.assertDefinitionsCount(1)
    }

    @Test
    fun `create a module with single`() {
        val app = koin {
            loadModules(
                module {
                    single { Simple.ComponentA() }
                })
        }

        app.assertDefinitionsCount(1)
    }

    @Test
    fun `create a complex single DI module`() {

        val app = koin {
            loadModules(
                module {
                    single { Simple.ComponentA() }
                    single { Simple.ComponentB(get()) }
                })
        }

        app.assertDefinitionsCount(2)
    }

    @Test
    fun `create a complex factory DI module`() {

        val app = koin {
            loadModules(
                module {
                    single { Simple.ComponentA() }
                    single { Simple.ComponentB(get()) }
                    factory { Simple.ComponentC(get()) }
                })
        }

        app.assertDefinitionsCount(3)
    }

    @Test
    fun `create several modules`() {

        val app = koin {
            loadModules(
                module {
                    single { Simple.ComponentA() }
                },
                module {
                    single { Simple.ComponentB(get()) }
                })
        }

        app.assertDefinitionsCount(2)
    }
}