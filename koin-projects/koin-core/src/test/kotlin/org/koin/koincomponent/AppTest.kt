package org.koin.koincomponent

import org.junit.Assert
import org.junit.Test
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.inject
import org.koin.dsl.module

class TasksView
class TasksPresenter(val view: TasksView)

class MyApp : KoinComponent {
    val view: TasksView by inject()
    val presenter: TasksPresenter by inject()
}

class AppTest {

    @Test
    fun `can run KoinComponent app`() {
        val app = startKoin {
            printLogger()
            modules(
                module {
                    single { TasksView() }
                    single { TasksPresenter(get()) }
                })
        }

        val koin = app.koin
        val myApp = MyApp()

        Assert.assertEquals(myApp.presenter.view, myApp.view)
        Assert.assertEquals(myApp.presenter, koin.get<TasksPresenter>())

        stopKoin()
    }
}