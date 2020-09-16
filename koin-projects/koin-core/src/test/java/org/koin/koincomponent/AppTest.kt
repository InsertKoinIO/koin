package org.koin.koincomponent

import org.junit.Assert
import org.junit.Test
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class TasksView
class TasksPresenter(val view: TasksView)

@OptIn(KoinApiExtension::class)
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