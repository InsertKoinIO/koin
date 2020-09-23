package org.koin.koincomponent

import org.junit.Test
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

class TODOAppTest {

    val todoAppModule = module {
        single { TasksView() } bind TasksContract.View::class
        single { TasksPresenter(get()) } bind TasksContract.Presenter::class
    }

    val repositoryModule = module {
        single(named("remoteDataSource")) { FakeTasksRemoteDataSource() } bind TasksDataSource::class
        single(named("localDataSource")) { TasksLocalDataSource() } bind TasksDataSource::class
        single {
            TasksRepository(
                    get(named("remoteDataSource")),
                    get(named("localDataSource"))
            )
        } bind TasksDataSource::class
    }

    interface TasksContract {
        interface View
        interface Presenter
    }

    @OptIn(KoinApiExtension::class)
    class TasksView : KoinComponent, TasksContract.View {
        val taskPreenter by inject<TasksContract.Presenter>()
    }

    class TasksPresenter(val tasksRepository: TasksRepository) : TasksContract.Presenter
    interface TasksDataSource
    class FakeTasksRemoteDataSource : TasksDataSource
    class TasksLocalDataSource : TasksDataSource
    class TasksRepository(
            val remoteDataSource: TasksDataSource,
            val localDatasource: TasksDataSource
    ) : TasksDataSource

    @Test
    fun `should create all components`() {
        val koinApp = startKoin {
            printLogger(Level.DEBUG)
            modules(todoAppModule + repositoryModule)
        }
        val koin = koinApp.koin

        val view = koin.get<TasksView>()
        println("-> ${view.taskPreenter}")
        stopKoin()
    }
}