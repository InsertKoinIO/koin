package org.koin.core.instance

import org.junit.Assert
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.dsl.single

class ScopeDelcarationTest {

    @Test
    fun `can't get scoped dependency without scope from single`() {
        val scopeName = named("MY_SCOPE")

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        single<ComponentA>()

                        scope(scopeName) {
                            single<ComponentB>()
                        }
                    }
            )
        }.koin

        val scope = koin.createScope("scope", scopeName)
        Assert.assertEquals(koin.get<ComponentA>(), scope.get<ComponentB>().a)
    }

}