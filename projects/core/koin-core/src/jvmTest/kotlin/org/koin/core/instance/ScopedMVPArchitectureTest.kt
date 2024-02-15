package org.koin.core.instance

import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.dsl.bind
import org.koin.dsl.factory
import org.koin.dsl.module
import org.koin.dsl.single

class ScopedMVPArchitectureTest {

    val MVPModule = module {
        single<Repository>()
        single<View>()
        factory<Presenter>()
    }

    val DataSourceModule = module {
        single<DebugDatasource>() bind Datasource::class
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `should create all MVP hierarchy`() {
        stopKoin()
        val koin = startKoin {
            printLogger(Level.DEBUG)
            modules(MVPModule + DataSourceModule)
        }.koin

        val view = koin.get<View>()
        val presenter = koin.get<Presenter>()
        val repository = koin.get<Repository>()
        val datasource = koin.get<Datasource>()

        Assert.assertNotEquals(presenter, view.presenter)
        Assert.assertEquals(repository, presenter.repository)
        Assert.assertEquals(repository, view.presenter.repository)
        Assert.assertEquals(datasource, repository.datasource)
    }
}
