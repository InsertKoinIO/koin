package org.koin.core

import org.koin.Simple
import org.koin.core.logger.Level
import org.koin.core.module.dsl.scopedOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test

class ScopeInfoTest {

    @Test
    fun resolve_via_a_scope(){

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    factory { Simple.ComponentC(get()) }
                    single { Simple.ComponentA() }
                    scope<Simple.ComponentA> {
                        scopedOf(Simple::ComponentB)
                    }
                }
            )
        }.koin

        val a = koin.get<Simple.ComponentA>()
        val scope = koin.createScope<Simple.ComponentA>("_a_",a)
        val b = scope.get<Simple.ComponentB>()
        scope.get<Simple.ComponentC> { parametersOf(b) }
    }
}