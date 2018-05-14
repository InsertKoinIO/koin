package org.koin.test.scope

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.dumpScopes
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertDefinedInScope

class MVPScopedTest : AutoCloseKoinTest() {

    val mainModule = module("org.koin") {

        single { Repository() }

        module("view") {
            single { Presenter(get()) }
        }
    }

    class Repository()
    class View : KoinComponent {
        val presenter by inject<Presenter>()
    }

    class Presenter(val repository: Repository)

    @Test
    fun `inject view`() {
        startKoin(listOf(mainModule))
        dumpScopes()

        assertDefinedInScope(Repository::class, "koin")
        assertDefinedInScope(Presenter::class, "view")

        val view = View()
        Assert.assertEquals(view.presenter, get<Presenter>())
    }
}