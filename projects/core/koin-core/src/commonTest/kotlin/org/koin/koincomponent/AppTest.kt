package org.koin.koincomponent

import org.koin.KoinCoreTest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals

class TasksView
class TasksPresenter(val view: TasksView)

class MyApp : KoinComponent {
    val view: TasksView by inject()
    val presenter: TasksPresenter by inject()
}

class AppTest : KoinCoreTest(){

    @Test
    fun can_run_KoinComponent_app() {
        val app = startKoin {
            printLogger()
            modules(
                module {
                    single { TasksView() }
                    single { TasksPresenter(get()) }
                },
            )
        }

        val koin = app.koin
        val myApp = MyApp()

        assertEquals(myApp.presenter.view, myApp.view)
        assertEquals(myApp.presenter, koin.get<TasksPresenter>())

        stopKoin()
    }
}
