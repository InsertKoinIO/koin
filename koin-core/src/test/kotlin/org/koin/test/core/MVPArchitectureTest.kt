package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.test.ext.assertContexts
import org.koin.test.ext.assertDefinedInScope
import org.koin.test.ext.assertDefinitions
import org.koin.test.ext.assertRemainingInstances

/**
 * Created by arnaud on 01/06/2017.
 */
class MVPArchitectureTest {

    class MVPModule : Module() {
        override fun context() =
                applicationContext {
                    provide { Repository(get()) }

                    context("ViewContext") {
                        provideFactory { View(get()) }
                        provideFactory { Presenter(get()) }
                    }
                }
    }

    class DataSourceModule : Module() {
        override fun context() =
                applicationContext {
                    provide { DebugDatasource() } bind (Datasource::class)
                }
    }


    class View(val presenter: Presenter)
    class Presenter(val repository: Repository)
    class Repository(val datasource: Datasource)
    interface Datasource
    class DebugDatasource : Datasource

    @Test
    fun `should create all MVP hierarchy`() {
        val ctx = Koin().build(MVPModule(), DataSourceModule())

        val view = ctx.get<View>()
        val presenter = ctx.get<Presenter>()
        val repository = ctx.get<Repository>()
        val datasource = ctx.get<DebugDatasource>()

        Assert.assertNotEquals(presenter, view.presenter)
        Assert.assertEquals(repository, presenter.repository)
        Assert.assertEquals(repository, view.presenter.repository)
        Assert.assertEquals(datasource, repository.datasource)

        ctx.assertRemainingInstances(2)
        ctx.assertDefinitions(4)
        ctx.assertContexts(2)
        ctx.assertDefinedInScope(Repository::class, Scope.ROOT)
        ctx.assertDefinedInScope(DebugDatasource::class, Scope.ROOT)
        ctx.assertDefinedInScope(View::class, "ViewContext")
        ctx.assertDefinedInScope(Presenter::class, "ViewContext")
    }

}