package org.koin.experimental.builder

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.logger.Level
import org.koin.core.standalone.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

class ScopedMVPArchitectureTest : AutoCloseKoinTest() {

    val MVPModule = module {
        single<Repository>()
        single<View>()
        scope<Presenter>("session")
    }

    val DataSourceModule = module {
        single<DebugDatasource>() bind Datasource::class
    }

    @Test
    fun `should create all MVP hierarchy`() {
        startKoin {
            logger(Level.DEBUG)
            modules(MVPModule, DataSourceModule)
        }

        val view = get<View>()
        val presenter = get<Presenter>()
        val repository = get<Repository>()
        val datasource = get<Datasource>()

        Assert.assertEquals(presenter, view.presenter)
        Assert.assertEquals(repository, presenter.repository)
        Assert.assertEquals(repository, view.presenter.repository)
        Assert.assertEquals(datasource, repository.datasource)
    }

    @Test
    fun `should handle scope`() {
        startKoin {
            logger(Level.DEBUG)
            modules(MVPModule, DataSourceModule)
        }

        val view = get<View>()
        view.onDestroy()

        try {
            get<Presenter>()
            fail("should not get presenter anymore")
        } catch (e: ScopeNotCreatedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `check MVP hierarchy`() {
        startKoin {
            logger(Level.DEBUG)
            modules(MVPModule, DataSourceModule)
        }
    }
}