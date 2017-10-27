package org.koin.test

import org.junit.Assert
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.standalone.releaseContext
import org.koin.test.components.KoinTest
import org.koin.test.components.getKoin
import org.koin.test.components.inject
import org.koin.test.components.startContext
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinedInScope
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class TestSample : KoinTest {

    class MVPModule : Module() {
        override fun context() =
                applicationContext {
                    provide { Repository(get()) }

                    context("View") {
                        provide { View(get()) }
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


    class View(val presenter: Presenter) {
        fun onDestroy() {
            releaseContext("View")
        }
    }

    class Presenter(val repository: Repository)
    class Repository(val datasource: Datasource)
    interface Datasource
    class DebugDatasource : Datasource


    val view by inject<View>()
    val presenter by inject<Presenter>()
    val repository by inject<Repository>()
    val datasource by inject<DebugDatasource>()

    @Test
    fun `should create all MVP hierarchy`() {
        startContext(MVPModule(), DataSourceModule())

        val ctx = getKoin()

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