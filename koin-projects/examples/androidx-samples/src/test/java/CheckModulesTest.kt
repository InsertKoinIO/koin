import org.junit.Test
import org.koin.sample.androidx.di.appModule
import org.koin.sample.androidx.di.mvpModule
import org.koin.sample.androidx.di.mvvmModule
import org.koin.sample.androidx.di.scopeModule
import org.koin.test.check.checkModules

class CheckModulesTest {

    @Test
    fun `test DI modules`() = checkModules {
        modules(appModule + mvpModule + mvvmModule + scopeModule)
    }

}