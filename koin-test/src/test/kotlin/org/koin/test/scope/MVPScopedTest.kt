package org.koin.test.scope

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.dumpModulePaths
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertIsInModulePath

class MVPScopedTest : AutoCloseKoinTest() {

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

    class Repository()
    class Presenter(val repository: Repository)
    class View : KoinComponent {
        val presenter by inject<Presenter>()
    }

    @Test
    fun `inject view`() {
        startKoin(listOf(mainModule))
        dumpModulePaths()

        assertIsInModulePath(Repository::class, "koin")
        assertIsInModulePath(Presenter::class, "view")

        val view = View()
        Assert.assertEquals(view.presenter, get<Presenter>())
    }

    @Test
    fun `inject view from A B C`() {
        startKoin(listOf(module2))
        dumpModulePaths()

        assertIsInModulePath(Repository::class, "B")
        assertIsInModulePath(Presenter::class, "C")

        val view = View()
        Assert.assertEquals(view.presenter, get<Presenter>())
    }
}