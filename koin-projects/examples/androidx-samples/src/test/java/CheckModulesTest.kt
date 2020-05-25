import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.core.parameter.parametersOf
import org.koin.sample.androidx.components.mvp.FactoryPresenter
import org.koin.sample.androidx.components.mvp.ScopedPresenter
import org.koin.sample.androidx.di.appModule
import org.koin.sample.androidx.di.mvpModule
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules

@Category(CheckModuleTest::class)
class CheckModulesTest {

    @Test
    fun `test DI modules`() = checkModules(
            parameters = {
                create<FactoryPresenter> { parametersOf("_ID_") }
                create<ScopedPresenter> { parametersOf("_ID_") }
            }
    ) {
        modules(appModule + mvpModule)// + mvpModule + mvvmModule + scopeModule)
    }

}