package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AbstractKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinedInScope
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class MVPArchitectureTest : AbstractKoinTest() {

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
        startKoin(listOf(MVPModule(), DataSourceModule()))

        val view = get<View>()
        val presenter = get<Presenter>()
        val repository = get<Repository>()
        val datasource = get<DebugDatasource>()

        Assert.assertNotEquals(presenter, view.presenter)
        Assert.assertEquals(repository, presenter.repository)
        Assert.assertEquals(repository, view.presenter.repository)
        Assert.assertEquals(datasource, repository.datasource)

        assertRemainingInstances(2)
        assertDefinitions(4)
        assertContexts(2)
        assertDefinedInScope(Repository::class, Scope.ROOT)
        assertDefinedInScope(DebugDatasource::class, Scope.ROOT)
        assertDefinedInScope(View::class, "ViewContext")
        assertDefinedInScope(Presenter::class, "ViewContext")
    }

}