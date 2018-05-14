package org.koin.test.standalone

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertRemainingInstances

class TODOAppTest : AutoCloseKoinTest() {

    val TodoAppModule = module {
        single { TasksView() } bind TasksContract.View::class
        single { TasksPresenter(get()) as TasksContract.Presenter }
    }

    val RepositoryModule = module {
        single("remoteDataSource") { FakeTasksRemoteDataSource() as TasksDataSource }
        single("localDataSource") { TasksLocalDataSource() as TasksDataSource }
        single { TasksRepository(get("remoteDataSource"), get("localDataSource")) } bind TasksDataSource::class
    }

    interface TasksContract {
        interface View
        interface Presenter
    }

    class TasksView() : KoinComponent, TasksContract.View {
        val taskPreenter by inject<TasksContract.Presenter>()
    }

    class TasksPresenter(val tasksRepository: TasksRepository) : TasksContract.Presenter
    interface TasksDataSource
    class FakeTasksRemoteDataSource() : TasksDataSource
    class TasksLocalDataSource() : TasksDataSource
    class TasksRepository(val remoteDataSource: TasksDataSource, val localDatasource: TasksDataSource) : TasksDataSource

    @Test
    fun `should create all components`() {
        startKoin(listOf(TodoAppModule, RepositoryModule))

        val view = get<TasksView>()
        Assert.assertNotNull(view.taskPreenter)
        assertRemainingInstances(5)
    }
}