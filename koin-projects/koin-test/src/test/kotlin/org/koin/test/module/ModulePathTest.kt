package org.koin.test.module

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.dumpModulePaths
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertIsInModulePath

class ModulePathTest : AutoCloseKoinTest() {

    val mainModule = module {
        module("org.koin") {
            single { Repository() }

            module("view") {
                single { Presenter(get()) }
            }
        }
    }

    val module2 = module {
        module(path = "A") {
            module(path = "B") {
                single { Repository() }
                module(path = "C") {
                    single { Presenter(get()) }
                }
            }
        }
    }

    val module3 = module {
        module(path = "A") {
            module(path = "B") {
                single { Repository() }
                module(path = "C") {
                    single { Presenter(get()) }
                }

                module(path = "D") {
                    single { Presenter2(get()) }
                }
            }
        }
    }

    class Repository()
    class Presenter(val repository: Repository)
    class Presenter2(val repository: Repository)
    class View : KoinComponent {
        val presenter by inject<Presenter>()
    }

    @Test
    fun `inject view`() {
        startKoin(listOf(mainModule))
        dumpModulePaths()

        assertIsInModulePath(Repository::class, "koin")
        assertIsInModulePath(Presenter::class, "view")
        assertContexts(4)
        val view = View()
        Assert.assertEquals(view.presenter, get<Presenter>())
    }

    @Test
    fun `inject view from A B C`() {
        startKoin(listOf(module2))
        dumpModulePaths()

        assertIsInModulePath(Repository::class, "B")
        assertIsInModulePath(Presenter::class, "C")

        assertContexts(4)

        val view = View()
        Assert.assertEquals(view.presenter, get<Presenter>())
    }

    @Test
    fun `inject view from A B C D`() {
        startKoin(listOf(module3))
        dumpModulePaths()

        assertIsInModulePath(Repository::class, "B")
        assertIsInModulePath(Presenter::class, "C")
        assertIsInModulePath(Presenter2::class, "D")

        assertContexts(5)

        val view = View()
        Assert.assertEquals(view.presenter, get<Presenter>())
    }
}