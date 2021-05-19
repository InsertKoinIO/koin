import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkerParameters
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.sample.androidx.di.*
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProvider
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito

class CheckModulesTest {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `test DI modules`() =
        checkModules(
            allowedMocks = listOf(
                WorkerParameters::class,
                SavedStateHandle::class
            ),
            allowedExceptions = listOf(
                NullPointerException::class
            )
        ){
            androidContext(MockProvider.makeMock<Application>())
            modules(allModules)
        }
}