import io.mockk.mockkClass
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.core.logger.Level
import org.koin.sample.android.di.allModules
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule

@Category(CheckModuleTest::class)
class CheckModulesTest {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @Test
    fun `test DI modules`() =
        checkModules {
            printLogger(Level.DEBUG)
            modules(allModules)
        }
}