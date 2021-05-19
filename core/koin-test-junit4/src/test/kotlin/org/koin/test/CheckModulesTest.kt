package org.koin.test

import org.junit.After
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.dsl.single
import org.koin.test.check.MockParametersHolder
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import java.util.*

class CheckModulesTest {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @After
    fun after(){
        stopKoin()
    }

    @Test
    fun `check a scoped module`() {
        val modules = module {
            scope(named("scope")) {
                scoped { Simple.ComponentA() }
                scoped { Simple.ComponentB(get()) }
            }
        }
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                modules
            )
        }.checkModules()
    }

    @Test
    fun `check a scoped module and ext deps - failed `() {
        try {
            koinApplication {
                printLogger(Level.DEBUG)
                modules(
                    module {
                        single { Simple.ComponentB(get()) }
                        scope(named("scope")) {
                            scoped { Simple.ComponentA() }
                        }
                    }
                )
            }.checkModules()
            fail()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    @Test
    fun `check a scoped module and ext scope - failed`() {
        try {
            koinApplication {
                printLogger(Level.DEBUG)
                modules(
                    module {
                        scope(named("scope2")) {
                            scoped { Simple.ComponentB(get()) }
                        }
                        scope(named("scope1")) {
                            scoped { Simple.ComponentA() }
                        }
                    }
                )
            }.checkModules()
            fail()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    @Test
    fun `check a simple module`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { Simple.ComponentA() }
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a module with link`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { Simple.ComponentA() }
                    single { Simple.ComponentB(get()) }
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a broken module`() {
        try {
            koinApplication {
                printLogger(Level.DEBUG)
                modules(
                    module {
                        single { Simple.ComponentB(get()) }
                    }
                )
            }.checkModules()
            fail("should not pass with borken definitions")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `check a module with params`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { (s: String) -> Simple.MyString(s) }
                    single(UpperCase) { (s: String) -> Simple.MyString(s.uppercase(Locale.getDefault())) }
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a module with params using create method with KClass`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { (s: String) -> Simple.MyString(s) }
                    single(UpperCase) { (s: String) -> Simple.MyString(s.uppercase(Locale.getDefault())) }
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a module with params - auto`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { (s: String) -> Simple.MyString(s) }
                    single { (a: Simple.ComponentA) -> Simple.ComponentB(a) }
                }
            )
        }.checkModules(
            allowedMocks = listOf(
                Simple.ComponentA::class
            )
        )
    }

    @Test
    fun `check a module with params - default mocked object`() {
        var injectedValue: Simple.ComponentA? = null
        startKoin {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { (a: Simple.ComponentA) ->
                        injectedValue = a
                        Simple.ComponentB(a)
                    }
                }
            )
        }.checkModules(
            allowedMocks = listOf(
                Simple.ComponentA::class
            )
        )

        assert(injectedValue != null)
    }

    @Test
    fun `check a module with params - default value object`() {
        var injectedValue: Simple.ComponentA? = null
        val componentA = Simple.ComponentA()
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { (a: Simple.ComponentA) ->
                        injectedValue = a
                        Simple.ComponentB(a)
                    }
                }
            )
        }.checkModules(
            defaultValues = mapOf(
                Simple.ComponentA::class to componentA
            )
        )

        assert(injectedValue == componentA)
    }

    @Test
    fun `check a module with params - auto scope`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(
                module {
                    scope<Simple.ComponentA> {
                        scoped { Simple.ComponentB(it.get()) }
                    }
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a module with params - auto scope - error`() {
        try {
            koinApplication {
                printLogger(Level.DEBUG)
                modules(
                    module {
                        scope<Simple.ComponentA> {
                            scoped { Simple.ComponentC(get()) }
                        }
                    }
                )
            }.checkModules()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `check with qualifier`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(module {
                single(named("test")) { Simple.ComponentA() }
            })
        }.checkModules()
    }

    @Test
    fun `check a module with property`() {
        koinApplication {
            printLogger(Level.DEBUG)
            properties(hashMapOf("aValue" to "value"))
            modules(
                module {
                    single { Simple.MyString(getProperty("aValue")) }
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a module with params - new instance API`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single<Simple.ComponentB>()
                }
            )
        }.checkModules(
            allowedMocks = listOf(
                Simple.ComponentA::class
            )
        )
    }

    @Test
    fun `check a module with params - param`() {
        try {
            startKoin {
                printLogger(Level.DEBUG)
                modules(
                    module {
                        single { Simple.ComponentB(it.get()) }
                    }
                )
            }.checkModules()
            fail()
        } catch (e: Exception) {
        }
    }
}