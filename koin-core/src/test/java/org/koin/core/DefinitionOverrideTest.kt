package org.koin.core

import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.Simple
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.assertDefinitionsCount

class DefinitionOverrideTest {

    @Test
    fun `allow overrides by type`() {

        val app = koinApplication {
            modules(
                    module {
                        single<Simple.ComponentInterface1> { Simple.Component2() }
                        single<Simple.ComponentInterface1>(override = true) { Simple.Component1() }
                    }
            )
        }

        app.assertDefinitionsCount(1)
        assertTrue(app.koin.get<Simple.ComponentInterface1>() is Simple.Component1)
    }

    @Test
    fun `allow overrides by name`() {

        val app = koinApplication {
            modules(
                    module {
                        single<Simple.ComponentInterface1>(named("DEF")) { Simple.Component2() }
                        single<Simple.ComponentInterface1>(named("DEF"), override = true) { Simple.Component1() }
                    }
            )
        }

        app.assertDefinitionsCount(1)
        assertTrue(app.koin.get<Simple.ComponentInterface1>(named("DEF")) is Simple.Component1)
    }
}