package org.koin.spek

import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject

class HelloService
class WorldService(val service: HelloService)

val module = applicationContext {
    factory { HelloService() }
    bean { WorldService(get()) }
}

class KoinDSLTest : AutoCloseKoinSpek({
    describe("DSL") {
        it("is possible to use inject") {
            StandAloneContext.startKoin(listOf(module))

            val worldService: WorldService by inject()
            Assert.assertNotNull(worldService.service)
        }

        it("is possible to use dryRun") {
            dryRun()
        }
    }
})