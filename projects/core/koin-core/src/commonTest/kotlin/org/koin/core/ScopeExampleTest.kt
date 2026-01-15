package org.koin.core

import org.koin.Simple
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.getOrCreateScope
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.error.ClosedScopeException
import org.koin.core.logger.Level
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.*

@OptIn(KoinInternalApi::class)
class ScopeExampleTest {

    class HttpClient(val name: String)

    class ScopedApi(val client: HttpClient)
    class SingleApi(val client: HttpClient)
    class ScopedRepo(
        val singleApi: SingleApi,
        val scopedApi: ScopedApi,
    )
    class SingleRepo(
        val singleApi: SingleApi,
    )
    class UseCaseOnScopedRepo(val scopedRepo: ScopedRepo)
    class UseCaseOnSingleRepo(val singleRepo: SingleRepo)

    @Test
    fun test_issue_2325() {
        val koin = startKoin {
            printLogger(level = Level.DEBUG)

            modules(
                module {
                    scope(named("name")) {
                        scoped { HttpClient("scoped") }
                        scoped { ScopedApi(get()) }
                        scoped { ScopedRepo(get(), get()) }
                    }
                    factory { UseCaseOnScopedRepo(getScope("id").get()) }

                    single { HttpClient("single") }
                    single { SingleApi(get()) }
                    single { SingleRepo(get()) }
                    factory { UseCaseOnSingleRepo(get()) }
                }
            )
        }.koin
        koin.createScope("id", named("name"))

        val ucOnScoped = koin.get<UseCaseOnScopedRepo>()
        assertEquals("single",ucOnScoped.scopedRepo.singleApi.client.name)
        assertEquals("scoped",ucOnScoped.scopedRepo.scopedApi.client.name)

        val ucOnSingle = koin.get<UseCaseOnSingleRepo>()
        assertEquals("single",ucOnSingle.singleRepo.singleApi.client.name)
    }

    class MyFactory(val id : String)

    @Test
    fun test_issue_scope_inj_param() {
        val koin = startKoin {
            printLogger(level = Level.DEBUG)

            modules(
                module {
                    // best
//                    factory { MyFactory(it.get()) }

                    // should work too
                    factoryOf(::MyFactory)
                }
            )
        }.koin

        val scope = koin.createScope("myScopeId", named("myScope"))
        val f1 = scope.get<MyFactory> { parametersOf("42") }
        val f2 = scope.get<MyFactory> { parametersOf("42") }

        assertEquals(f1.id,f2.id)
        assertNotEquals(f1,f2)

        assertTrue("Scope params stack is cleared") { scope.parameterStack?.get()?.isEmpty() ?: true }
        assertTrue("Root Scope params stack is cleared") { koin.scopeRegistry.rootScope.parameterStack?.get()?.isEmpty() ?: true }
    }
}
