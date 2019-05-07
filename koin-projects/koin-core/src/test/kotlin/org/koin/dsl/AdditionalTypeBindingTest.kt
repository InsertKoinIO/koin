package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Simple
import org.koin.core.qualifier.named
import org.koin.test.assertDefinitionsCount

class AdditionalTypeBindingTest {

    @Test
    fun `can resolve an additional type`() {
        val app = koinApplication {
            printLogger()
            modules(
                    module {
                        single { Simple.Component1() } bind Simple.ComponentInterface1::class
                    })
        }

        app.assertDefinitionsCount(1)

        val koin = app.koin
        val c1 = koin.get<Simple.Component1>()
        val c = koin.bind<Simple.Component1, Simple.ComponentInterface1>()

        assertEquals(c1, c)
    }

    @Test
    fun `additional type conflict`() {
        val koin = koinApplication {
            printLogger()
            modules(
                    module {
                        single { Simple.Component1() } bind Simple.ComponentInterface1::class
                        single<Simple.ComponentInterface1> { Simple.Component1() }
                    })
        }.koin

        assert(koin.getAll<Simple.ComponentInterface1>().size == 2)
    }

    @Test
    fun `should not conflict name & default type`() {
        val app = koinApplication {
            printLogger()
            modules(
                    module {
                        single<Simple.ComponentInterface1>(named("default")) { Simple.Component2() }
                        single<Simple.ComponentInterface1> { Simple.Component1() }
                    })
        }
        val koin = app.koin
        koin.get<Simple.ComponentInterface1>(named("default"))
    }

    @Test
    fun `can resolve an additional types`() {
        val app = koinApplication {
            modules(
                    module {
                        single { Simple.Component1() } binds arrayOf(
                                Simple.ComponentInterface1::class,
                                Simple.ComponentInterface2::class
                        )
                    })
        }

        app.assertDefinitionsCount(1)

        val koin = app.koin
        val c1 = koin.get<Simple.Component1>()
        val ci1 = koin.bind<Simple.Component1, Simple.ComponentInterface1>()
        val ci2 = koin.bind<Simple.Component1, Simple.ComponentInterface2>()

        assertEquals(c1, ci1)
        assertEquals(c1, ci2)
    }

    @Test
    fun `conflicting with additional types`() {
        val koin = koinApplication {
            modules(
                    module {
                        single<Simple.ComponentInterface1> { Simple.Component2() }
                        single { Simple.Component1() } binds arrayOf(
                                Simple.ComponentInterface1::class,
                                Simple.ComponentInterface2::class
                        )
                    })
        }.koin

        assert(koin.getAll<Simple.ComponentInterface1>().size == 2)
    }

}