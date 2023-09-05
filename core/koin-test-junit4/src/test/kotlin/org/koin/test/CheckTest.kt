package org.koin.test

import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito

interface MyRepository
class A(val repository: MyRepository)

class CheckTest {
    internal object MyComponents {

        val modulee: Module = module {
            factory {
                A(
                    repository = get<MyRepository>(),
                )
            }
        }
    }

    class CheckModulesTest {

        @get:Rule
        val mockProvider = MockProviderRule.create { clazz ->
            Mockito.mock(clazz.java)
        }

        @Test
        fun `test DI modules`() {
            koinApplication {
                printLogger(Level.DEBUG)
                modules(MyComponents.modulee)
                checkModules {
                    withInstance<MyRepository>()
                }
            }
        }
    }
}
