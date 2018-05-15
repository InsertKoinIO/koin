package org.koin.test.scope

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest

class KoinComponentPathTest : AutoCloseKoinTest() {

    val module1 = module {
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

                module(path = "D") {
                    single { Presenter2(get()) }
                }
            }
        }
    }

    class Repository()
    class Presenter(val repository: Repository)
    class Presenter2(val repository: Repository)
    class View1 : KoinComponent {
        val repository by inject<Repository>(module = "org.koin")
        val presenter by inject<Presenter>(module = "org.koin.view")
    }

    class View2 : KoinComponent {
        val presenter by inject<Presenter>(module = "org.koin")
    }

    @Test
    fun `inject with module path`() {
        startKoin(listOf(module1))

        val view1 = View1()
        Assert.assertEquals(view1.presenter, get<Presenter>())
        Assert.assertEquals(view1.repository, get<Repository>())

        try {
            val view2 = View2()
            println("${view2.presenter}")
            fail()
        } catch (e: Exception) {
        }

        Assert.assertNotNull(get<Presenter>(module = "org.koin.view"))
        Assert.assertNotNull(get<Repository>(module = "org.koin"))
    }

    @Test
    fun `inject with module path 2`() {
        startKoin(listOf(module2))

        Assert.assertNotNull(get<Presenter>(module = "A.B.C"))
        Assert.assertNotNull(get<Presenter2>(module = "A.B.D"))
        Assert.assertNotNull(get<Repository>(module = "A.B"))
    }
}