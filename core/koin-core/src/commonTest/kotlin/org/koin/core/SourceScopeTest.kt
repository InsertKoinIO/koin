package org.koin.core

import org.koin.core.logger.Level
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertTrue

class SourceScopeTest {

    interface Ctx
    class AppCtx : Ctx
    class Act : Ctx
    class Presenter1(val appCtx: AppCtx)
    class Presenter2(val appCtx: Ctx)
    class Presenter3(val appCtx: Act)

    @Test
    fun return_right_ctx(){
        val module = module {
            single { AppCtx() } withOptions {
                bind<Ctx>()
                bind<AppCtx>()
            }
            scope<Act>{
                scoped { Presenter1(get()) }
                scoped { Presenter2(get()) }
                scoped { Presenter3(get()) }
            }
        }

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(module)
        }.koin

        val act = Act()
        val scope = koin.createScope<Act>("_scope_",act)
        val p1 = scope.get<Presenter1>()
        val p2 = scope.get<Presenter2>()
        val p3 = scope.get<Presenter3>()
        val source = scope.getSource<Ctx>()
        val appCtx = koin.get<AppCtx>()

        assertTrue { p1.appCtx == appCtx }
        assertTrue { source != appCtx }
        assertTrue { p2.appCtx == source }
        assertTrue { p3.appCtx == source }
    }
}