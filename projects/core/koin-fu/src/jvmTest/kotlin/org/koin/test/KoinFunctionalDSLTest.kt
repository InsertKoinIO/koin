package org.koin

import org.koin.core.component.getScopeId
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.time.inMs
import org.koin.dsl.fu.factory
import org.koin.dsl.fu.scoped
import org.koin.dsl.fu.single
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.perfModule400
import kotlin.test.Test
import kotlin.time.measureTime
import kotlin.time.measureTimedValue
import org.koin.test.Perfs.*
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class KoinFunctionalDSLTest {

    @Test
    fun testBigModules() {
        (1..10).forEach {
            resolve(::perfModule400,"normal - ctor dsl")
            resolve(::perfModule400_B, "reflect - koin-fu")
        }
    }

    private fun resolve(module: () -> Module, tag : String) {
        val kValue =
            measureTimedValue {
                koinApplication {
                    modules(module())
                }.koin
            }
        val koin = kValue.value

        val time = measureTime {
            koin.get<C31>()
            koin.get<B31>()
            koin.get<D31>()
            koin.get<A31>()
        }
        println("$tag - start: ${kValue.duration.inMs} ms")
        println("$tag - resolution: ${time.inMs} ms")
    }

    @Test
    fun testFunctionParams() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single(::B1)
                    single(::C1)
                    factory(::A2)
                }
            )
        }.koin
        assertNotNull(koin.getOrNull<B1> { parametersOf(A1()) })
        assertNotNull(koin.getOrNull<C1> { parametersOf(A1()) })
        assertNotEquals(koin.get<A2>(),koin.get<A2>())
    }

    @Test
    fun testScopes() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    scope<A1> {
                        scoped(::B1)
                    }
                }
            )
        }.koin
        val a = A1()
        val scope = koin.createScope<A1>(a.getScopeId(),a)
        assertNotNull(scope.getOrNull<B1>())
    }

    @Test
    fun testNullableInjection() {

        class B1OrNull(val a : A1? = null)

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    factory(::B1OrNull)
                }
            )
        }.koin
        assertNotNull(koin.getOrNull<B1OrNull>())
        assert(koin.getOrNull<B1OrNull>()?.a == null)
        assert(koin.getOrNull<B1OrNull>{ parametersOf(A1())}?.a != null)
    }
}