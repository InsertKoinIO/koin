package org.koin.test.standalone

import org.junit.Assert
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.standalone.StandAloneRegistry
import org.koin.standalone.inject
import org.koin.standalone.releaseContext
import org.koin.standalone.startContext
import org.koin.test.ext.assertContexts
import org.koin.test.ext.assertDefinedInScope
import org.koin.test.ext.assertDefinitions
import org.koin.test.ext.assertRemainingInstances

class MVPArchitectureTest {
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


    class View() {
        val presenter: Presenter by inject()

        fun onDestroy() {
            releaseContext("View")
        }
    }

    class Presenter(val repository: Repository)
    class Repository(val datasource: Datasource)
    interface Datasource
    class DebugDatasource : Datasource

    @Test
    fun `should create all MVP hierarchy`() {
        startContext(MVPModule(), DataSourceModule())

        val ctx = StandAloneRegistry.koinContext

        val view = ctx.get<View>()
        val presenter = ctx.get<Presenter>()
        val repository = ctx.get<Repository>()
        val datasource = ctx.get<DebugDatasource>()

        Assert.assertEquals(presenter, view.presenter)
        Assert.assertEquals(repository, presenter.repository)
        Assert.assertEquals(repository, view.presenter.repository)
        Assert.assertEquals(datasource, repository.datasource)

        ctx.assertRemainingInstances(4)
        ctx.assertDefinitions(4)
        ctx.assertContexts(2)
        ctx.assertDefinedInScope(Repository::class, Scope.ROOT)
        ctx.assertDefinedInScope(DebugDatasource::class, Scope.ROOT)
        ctx.assertDefinedInScope(View::class, "View")
        ctx.assertDefinedInScope(Presenter::class, "View")

        view.onDestroy()
        ctx.assertRemainingInstances(2)
        ctx.assertDefinitions(4)
        ctx.assertContexts(2)
    }
}