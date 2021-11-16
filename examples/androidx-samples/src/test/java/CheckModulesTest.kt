import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkerParameters
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.sample.androidx.di.*
import org.koin.test.check.checkKoinModules
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProvider
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito

class CheckModulesTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun `test DI modules`(){
        checkKoinModules(allTestModules){
            withInstance<Context>()
            withInstance<Activity>()
            withInstance<Application>()
            withInstance<SavedStateHandle>()
            withInstance<WorkerParameters>()
        }
    }
}