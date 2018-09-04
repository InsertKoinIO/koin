package org.koin.test.scope

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.log.PrintLogger
import org.koin.standalone.*
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.*

class MVPArchitectureTest : AutoCloseKoinTest() {

    val MVPModule = module {
        single { Repository(get()) }

        module("view") {
            single { View() }
            scope("session") { Presenter(get()) }
        }
    }

    val DataSourceModule = module {
        single { DebugDatasource() } bind Datasource::class
    }

    class View() : KoinComponent {
        val session = getKoin().createScope("session")
        val presenter: Presenter by inject()

        fun onDestroy() {
            session.close()
        }
    }

    class Presenter(val repository: Repository)
    class Repository(val datasource: Datasource)
    interface Datasource
    class DebugDatasource : Datasource

    @Test
    fun `should create all MVP hierarchy`() {
        startKoin(listOf(MVPModule, DataSourceModule),logger = PrintLogger(showDebug = true))

        val view = get<View>()
        val presenter = get<Presenter>()
        val repository = get<Repository>()
        val datasource = get<DebugDatasource>()

        Assert.assertEquals(presenter, view.presenter)
        Assert.assertEquals(repository, presenter.repository)
        Assert.assertEquals(repository, view.presenter.repository)
        Assert.assertEquals(datasource, repository.datasource)

        assertRemainingInstanceHolders(4)
        assertDefinitions(4)
        assertContexts(2)
        assertIsInRootPath(Repository::class)
        assertIsInRootPath(DebugDatasource::class)
        assertIsInModulePath(View::class, "view")
        assertIsInModulePath(Presenter::class, "view")

        view.onDestroy()
        assertRemainingInstanceHolders(3)
        assertDefinitions(4)
        assertContexts(2)
    }
}