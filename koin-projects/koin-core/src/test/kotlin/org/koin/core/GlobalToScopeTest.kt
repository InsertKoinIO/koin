package org.koin.core

import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class GlobalToScopeTest {

    @Test
    fun `get scoped dependency without scope`() {
        val koin = koinApplication {
            defaultLogger(Level.DEBUG)
            modules(
                    module {
                        scope<ClosedScopeAPI.ScopeType> {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }.koin

        try {
            koin.get<Simple.ComponentA>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        val scope = koin.createScopeWithType<ClosedScopeAPI.ScopeType>("myScope")
//        Assert.assertEquals(scope.get<Simple.ComponentB>(), scope.get<Simple.ComponentB>())
//        Assert.assertEquals(scope.get<Simple.ComponentA>(), scope.get<Simple.ComponentB>().a)
    }

    @Test
    fun `get scoped dependency without scope from single`() {
        val koin = koinApplication {
            defaultLogger(Level.DEBUG)
            modules(
                    module {
                        scope<ClosedScopeAPI.ScopeType> {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }.koin
//        val scope = koin.createScopeWithType<ClosedScopeAPI.ScopeType>("myScope")
//        Assert.assertEquals(scope.get<Simple.ComponentB>(), scope.get<Simple.ComponentB>())
//        Assert.assertEquals(scope.get<Simple.ComponentA>(), scope.get<Simple.ComponentB>().a)
    }

    @Test
    fun `get scoped dependency without scope from koin component`() {

    }
}