import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.core.logger.Level
import org.koin.sample.android.di.allModules
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules

@Category(CheckModuleTest::class)
class CheckModulesTest {

    @Test
    fun `test DI modules`() =
        checkModules {
            printLogger(Level.DEBUG)
            modules(allModules)
        }
}