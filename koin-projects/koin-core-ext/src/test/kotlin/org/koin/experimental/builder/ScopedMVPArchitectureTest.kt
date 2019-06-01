package org.koin.experimental.builder

import org.junit.Assert
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

class ScopedMVPArchitectureTest : AutoCloseKoinTest() {

    val MVPModule = module {
        single<Repository>()
        single<View>()
        factory<Presenter>()
    }

    val DataSourceModule = module {
        singleBy<Datasource, DebugDatasource>()
    }

    @Test
    fun `should create all MVP hierarchy`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(MVPModule+ DataSourceModule)
        }

        val view = get<View>()
        val presenter = get<Presenter>()
        val repository = get<Repository>()
        val datasource = get<Datasource>()

        Assert.assertNotEquals(presenter, view.presenter)
        Assert.assertEquals(repository, presenter.repository)
        Assert.assertEquals(repository, view.presenter.repository)
        Assert.assertEquals(datasource, repository.datasource)
    }

    @Test
    fun `check MVP hierarchy`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(MVPModule+ DataSourceModule)
        }
    }
}