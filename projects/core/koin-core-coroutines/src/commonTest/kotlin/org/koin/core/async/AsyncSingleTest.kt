package org.koin.core.async

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class Config(val url: String)
class Database(val config: Config)
class Repository(val database: Database)

class AsyncSingleTest {

    @Test
    fun async_single_is_created_once() {
        runTest {
            var creations = 0
            val koin = koinApplication {
                modules(module { singleAsync { creations++; Config("db://local") } })
            }.koin

            val first = koin.getAsync<Config>()
            val second = koin.getAsync<Config>()

            assertSame(first, second)
            assertEquals(1, creations)
        }
    }

    @Test
    fun concurrent_callers_share_one_creation() {
        runTest {
            var creations = 0
            val koin = koinApplication {
                modules(module {
                    singleAsync {
                        creations++
                        delay(100)
                        Config("db://local")
                    }
                })
            }.koin

            val results = List(10) { async { koin.getAsync<Config>() } }.awaitAll()

            assertEquals(1, creations)
            assertEquals(1, results.toSet().size)
        }
    }

    @Test
    fun definition_runs_in_caller_coroutine_context() {
        runTest {
            var builderName: CoroutineName? = null
            var builderDispatcher: ContinuationInterceptor? = null
            val koin = koinApplication {
                modules(module {
                    singleAsync {
                        builderName = currentCoroutineContext()[CoroutineName]
                        builderDispatcher = currentCoroutineContext()[ContinuationInterceptor]
                        Config("db://local")
                    }
                })
            }.koin

            withContext(CoroutineName("caller")) { koin.getAsync<Config>() }

            assertEquals(CoroutineName("caller"), builderName)
            assertSame(coroutineContext[ContinuationInterceptor], builderDispatcher)
        }
    }

    @Test
    fun failed_creation_is_not_cached() {
        runTest {
            var attempts = 0
            val koin = koinApplication {
                modules(module {
                    singleAsync {
                        attempts++
                        if (attempts == 1) error("network down")
                        Config("db://local")
                    }
                })
            }.koin

            assertFailsWith<IllegalStateException> { koin.getAsync<Config>() }
            val config = koin.getAsync<Config>()

            assertEquals("db://local", config.url)
            assertEquals(2, attempts)
        }
    }

    @Test
    fun async_single_resolves_async_and_sync_dependencies() {
        runTest {
            val koin = koinApplication {
                modules(module {
                    single { Config("db://local") }
                    singleAsync { delay(10); Database(get()) }
                    singleAsync { Repository(getAsync()) }
                })
            }.koin

            val repository = koin.getAsync<Repository>()

            assertSame(koin.getAsync<Database>(), repository.database)
            assertSame(koin.get<Config>(), repository.database.config)
        }
    }

    @Test
    fun qualified_async_singles_are_distinct() {
        runTest {
            val koin = koinApplication {
                modules(module {
                    singleAsync(named("local")) { Config("db://local") }
                    singleAsync(named("remote")) { Config("db://remote") }
                })
            }.koin

            val local = koin.getAsync<Config>(named("local"))
            val remote = koin.getAsync<Config>(named("remote"))

            assertNotSame(local, remote)
            assertEquals("db://remote", remote.url)
        }
    }

    @Test
    fun recursive_request_fails_fast_instead_of_deadlocking() {
        runTest {
            val koin = koinApplication {
                modules(module {
                    singleAsync { Database(getAsync<Repository>().database.config) }
                    singleAsync { Repository(getAsync()) }
                })
            }.koin

            val failure = assertFailsWith<IllegalStateException> { koin.getAsync<Repository>() }
            assertEquals(true, failure.message?.startsWith("Circular dependency"))
        }
    }
}
