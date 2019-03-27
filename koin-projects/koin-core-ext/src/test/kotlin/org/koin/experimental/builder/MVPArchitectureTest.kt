package org.koin.experimental.builder

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.KoinTestRule
import org.koin.test.check.checkModules
import org.koin.test.get

class MVPArchitectureTest {
    val MVPModule = module {
        single<Repository>()
        single<View>()
        single<Presenter>()
    }

    val DataSourceModule = module {
        single<DebugDatasource>() bind Datasource::class
    }

    @get:Rule
    val rule = KoinTestRule.create {
        logger(Level.DEBUG)
        modules(MVPModule, DataSourceModule)
    }

    @Test
    fun `should create all MVP hierarchy`() {
        val view = rule.get<View>()
        val presenter = rule.get<Presenter>()
        val repository = rule.get<Repository>()
        val datasource = rule.get<Datasource>()

        Assert.assertEquals(presenter, view.presenter)
        Assert.assertEquals(repository, presenter.repository)
        Assert.assertEquals(repository, view.presenter.repository)
        Assert.assertEquals(datasource, repository.datasource)
    }

    @Test
    fun `check MVP hierarchy`() {
        rule.koinApplication.checkModules()
    }
}
