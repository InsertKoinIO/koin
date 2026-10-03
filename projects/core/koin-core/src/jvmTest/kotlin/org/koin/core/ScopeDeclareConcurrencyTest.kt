package org.koin.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ScopeDeclareConcurrencyTest {

    class DynamicInjectable(val value: String)
    class ScopeTestClass(val injected: DynamicInjectable)

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun testConcurrentScopeDeclare() {
        runBlocking {
            val scopeQualifier = named("MyScope")
            val koinApp = startKoin {
                modules(
                    module {
                        scope(scopeQualifier) {
                            scoped { ScopeTestClass(get()) }
                        }
                    }
                )
            }
            val koin = koinApp.koin

            val tasks = (0 until 20).map { x ->
                async(Dispatchers.Default) {
                    val scope = koin.createScope("scope-$x", scopeQualifier)
                    val injectable = DynamicInjectable("value-$x")
                    scope.declare(injectable)

                    val resolved = scope.get<ScopeTestClass>()
                    assertEquals(injectable.value, resolved.injected.value)
                }
            }
            tasks.awaitAll()
        }
    }
}
