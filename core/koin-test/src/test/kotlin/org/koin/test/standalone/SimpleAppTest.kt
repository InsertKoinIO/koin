package org.koin.test.standalone

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertRemainingInstances

class SimpleAppTest : AutoCloseKoinTest() {

    val appModule = module {
        single { TasksView() }
        single { TasksPresenter(get()) }
    }

    class TasksView
    class TasksPresenter(val view: TasksView)

    val view by inject<TasksView>()
    val presenter by inject<TasksPresenter>()

    @Test
    fun `should create all components`() {
        startKoin(listOf(appModule))

        Assert.assertNotNull(view)
        Assert.assertNotNull(presenter)
        Assert.assertEquals(view, presenter.view)

        assertRemainingInstances(2)
    }
}