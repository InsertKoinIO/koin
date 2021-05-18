import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkerParameters
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
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

    // @Test
    // @Ignore
    // fun `test DI modules`() =
    //     checkModules(parameters = {
    //         defaultValue<SavedStateHandle>()
    //         defaultValue<WorkerParameters>()
    //     }) {
    //         androidContext(MockProvider.makeMock<Application>())
    //         printLogger(Level.DEBUG)
    //         modules(appModule + mvpModule + mvvmModule + scopeModule)
    //     }
}