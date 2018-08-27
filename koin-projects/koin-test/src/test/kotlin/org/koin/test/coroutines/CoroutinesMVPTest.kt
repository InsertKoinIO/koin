package org.koin.test.coroutines

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.standalone.release
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.*

class CoroutinesMVPTest : AutoCloseKoinTest() {

    val MVPModule = module {
        single { Repository(get()) }

        module("View") {
            single { View() }
            single { Presenter(get()) }
        }
    }

    val DataSourceModule = module {
        single { DebugDatasource() } bind (Datasource::class)
    }

    class View : KoinComponent {
        val presenter: Presenter by inject()

        fun onDestroy() {
            release("View")
        }
    }

    class Presenter(val repository: Repository)
    class Repository(val datasource: Datasource)
    interface Datasource
    class DebugDatasource : Datasource

    @Test
    fun `should create all MVP hierarchy`() = runBlocking {
        startKoin(listOf(MVPModule, DataSourceModule))

        val view = async { get<View>() }.await()
        async {
            val presenter = async { get<Presenter>() }.await()
            Assert.assertEquals(presenter, view.presenter)

            val repository = get<Repository>()
            val datasource = get<DebugDatasource>()
            Assert.assertEquals(repository, presenter.repository)
            Assert.assertEquals(repository, view.presenter.repository)
            Assert.assertEquals(datasource, repository.datasource)
        }.await()

        assertRemainingInstanceHolders(4)
        assertDefinitions(4)
        assertContexts(2)
        assertIsInRootPath(Repository::class)
        assertIsInRootPath(DebugDatasource::class)
        assertIsInModulePath(View::class, "View")
        assertIsInModulePath(Presenter::class, "View")

        view.onDestroy()
        assertRemainingInstanceHolders(2)
        assertDefinitions(4)
        assertContexts(2)
    }
}