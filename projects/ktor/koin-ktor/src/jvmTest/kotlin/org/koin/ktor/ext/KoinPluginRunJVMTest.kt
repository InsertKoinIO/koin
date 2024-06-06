package org.koin.ktor.ext

import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngineEnvironmentReloading
import io.ktor.server.engine.embeddedServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test

class KoinPluginRunJVMTest {
    @BeforeTest
    fun before(){
        stopKoin()
    }

    @Test
    @Ignore // socket exception on GH
    fun `should can reload`()  = runBlocking<Unit> {
        val koinModule = module {
            single<String> {
                "Reproduction test"
            }
        }
        val s = embeddedServer(
            CIO,
            module = {
                install(Koin) {
                    modules(koinModule)
                }
            },
        ).start(false)
        delay(500)

        // verify for can auto-reload
        (s.environment as ApplicationEngineEnvironmentReloading).reload()
        s.stop()
    }
}