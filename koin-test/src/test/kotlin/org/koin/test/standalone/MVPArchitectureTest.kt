package org.koin.test.standalone

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.log.PrintLogger
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.koin.standalone.releaseContext
import org.koin.standalone.startContext
import org.koin.test.KoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinedInScope
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances
import org.koin.test.get

class MVPArchitectureTest : KoinTest {
    class MVPModule : Module() {
        override fun context() =
                applicationContext {
                    provide { Repository(get()) }

                    context("View") {
                        provide { View() }
                        provide { Presenter(get()) }
                    }
                }
    }

    class DataSourceModule : Module() {
        override fun context() =
                applicationContext {
                    provide { DebugDatasource() } bind (Datasource::class)
                }
    }


    class View() : KoinComponent {
        val presenter: Presenter by inject()

        fun onDestroy() {
            releaseContext("View")
        }
    }

    class Presenter(val repository: Repository)
    class Repository(val datasource: Datasource)
    interface Datasource
    class DebugDatasource : Datasource

    init {
        Koin.logger = PrintLogger()
    }

    @Test
    fun `should create all MVP hierarchy`() {
        startContext(listOf(MVPModule(), DataSourceModule()))

        val view = get<View>()
        val presenter = get<Presenter>()
        val repository = get<Repository>()
        val datasource = get<DebugDatasource>()

        Assert.assertEquals(presenter, view.presenter)
        Assert.assertEquals(repository, presenter.repository)
        Assert.assertEquals(repository, view.presenter.repository)
        Assert.assertEquals(datasource, repository.datasource)

        assertRemainingInstances(4)
        assertDefinitions(4)
        assertContexts(2)
        assertDefinedInScope(Repository::class, Scope.ROOT)
        assertDefinedInScope(DebugDatasource::class, Scope.ROOT)
        assertDefinedInScope(View::class, "View")
        assertDefinedInScope(Presenter::class, "View")

        view.onDestroy()
        assertRemainingInstances(2)
        assertDefinitions(4)
        assertContexts(2)
    }
}