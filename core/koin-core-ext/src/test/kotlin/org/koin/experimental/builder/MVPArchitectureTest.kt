package org.koin.experimental.builder

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.check.checkModules
import org.koin.test.get

class MVPArchitectureTest : KoinTest {
    val MVPModule = module {
        single<Repository>()
        single<View>()
        single<Presenter>()
    }

    val DataSourceModule = module {
        singleBy<Datasource, DebugDatasource>()
    }

    @get:Rule
    val rule = KoinTestRule.create {
        printLogger(Level.DEBUG)
        modules(MVPModule + DataSourceModule)
    }

    @Test
    fun `should create all MVP hierarchy`() {
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
    fun `check MVP hierarchy`() {
        checkModules {
            MVPModule + DataSourceModule
        }
    }
}
