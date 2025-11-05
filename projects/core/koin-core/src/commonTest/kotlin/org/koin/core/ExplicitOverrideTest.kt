package org.koin.core

import org.koin.Simple
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.module.dsl.override
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.dsl.override
import org.koin.test.assertDefinitionsCount
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ExplicitOverrideTest {

    @Test
    fun `implicit override - dsl option`() {
        val app = koinApplication {

            allowOverride(true)
            modules(
                module {
                    single<Simple.ComponentInterface1> { Simple.Component2() }
                },
                module {
                    single<Simple.ComponentInterface1> { Simple.Component1() }
                },
            )
        }

        app.assertDefinitionsCount(1)
        assertTrue(app.koin.get<Simple.ComponentInterface1>() is Simple.Component1)
    }

    @Test
    fun `explicit override - dsl option`() {
        val app = koinApplication {

            allowOverride(false)
            modules(
                module {
                    single<Simple.ComponentInterface1> { Simple.Component2() }
                },
                module {
                    single<Simple.ComponentInterface1> { Simple.Component1() } withOptions {
                        override()
                    }
                },
            )
        }

        app.assertDefinitionsCount(1)
        assertTrue(app.koin.get<Simple.ComponentInterface1>() is Simple.Component1)
    }

    @Test
    fun `explicit override - dsl`() {
        val app = koinApplication {

            strictOverride()
            modules(
                module {
                    single<Simple.ComponentInterface1> { Simple.Component2() }
                },
                module {
                    single<Simple.ComponentInterface1> { Simple.Component1() }.override()
                },
            )
        }

        app.assertDefinitionsCount(1)
        assertTrue(app.koin.get<Simple.ComponentInterface1>() is Simple.Component1)
    }

    @Test
    fun `explicit override - bind dsl`() {
        val app = koinApplication {

            strictOverride()
            modules(
                module {
                    single { Simple.Component2() }.bind<Simple.ComponentInterface1>()
                },
                module {
                    single { Simple.Component1() }
                        .bind<Simple.ComponentInterface1>()
                        .override()
                },
            )
        }

        app.assertDefinitionsCount(3)
        assertTrue(app.koin.get<Simple.ComponentInterface1>() is Simple.Component1)
    }

    @Test
    fun `cannot override without explicit override flag`() {
        assertFailsWith<DefinitionOverrideException> {
            koinApplication {
                strictOverride()
                modules(
                    module {
                        single<Simple.ComponentInterface1> { Simple.Component1() }
                    },
                    module {
                        // No .override() flag - should fail
                        single<Simple.ComponentInterface1> { Simple.Component2() }
                    },
                )
            }
        }
    }
}
