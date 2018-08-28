package org.koin.test.builder

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.experimental.builder.create
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.standalone.release
import org.koin.test.AutoCloseKoinTest
import org.koin.test.check
import org.koin.test.ext.junit.*

class MVPArchitectureTest : AutoCloseKoinTest() {

    val MVPModule = module {
        single { create<Repository>() }

        module("view") {
            single { View() }
            single { create<Presenter>() }
        }
    }

    val DataSourceModule = module {
        single { DebugDatasource() } bind Datasource::class
    }

    class View() : KoinComponent {
        val presenter: Presenter by inject()

        fun onDestroy() {
            release("view")
        }
    }

    class Presenter(val repository: Repository)
    class Repository(val datasource: Datasource)
    interface Datasource
    class DebugDatasource : Datasource

    @Test
    fun `should create all MVP hierarchy`() {
        startKoin(listOf(MVPModule, DataSourceModule))

        val view = get<View>()
        val presenter = get<Presenter>()
        val repository = get<Repository>()
//        val dbgDS = get<DebugDatasource>()
        val datasource = get<Datasource>()

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
        assertRemainingInstanceHolders(4)
        assertDefinitions(4)
        assertContexts(2)
    }

    @Test
    fun `check MVP hierarchy`() {
        check(listOf(MVPModule, DataSourceModule))
    }
}