package org.koin.test

import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.check.checkKoinModules
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CheckModulesTest {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun `check a module - all dsl`() {
        val modules = module {
            single { p -> Simple.ComponentB(p.get()) }
            single(named("param")) { p -> Simple.MyString(p.get()) }
            single { Simple.MyString(getProperty("aValue")) }
            scope(named("scope1")) {
                scoped { Simple.ComponentD() }
            }
            scope(named("scope2")) {
                scoped { Simple.ComponentE(get()) }
            }
        }

        koinApplication {
            modules(modules)
            checkModules {
                withInstance<Simple.ComponentA>()
                //            withInstance<String>("a_parameter")
                withParameter<String> { "a_parameter" }
                withProperty("aValue", "string_value")
                withScopeLink(named("scope2"), named("scope1"))
            }
        }
    }

    @Test
    fun `check a module - all dsl - checkKoinModules`() {
        val modules = module {
            single { p -> Simple.ComponentB(p.get()) }
            single(named("param")) { p -> Simple.MyString(p.get()) }
            single { Simple.MyString(getProperty("aValue")) }
            scope(named("scope1")) {
                scoped { Simple.ComponentD() }
            }
            scope(named("scope2")) {
                scoped { Simple.ComponentE(get()) }
            }
        }

        checkKoinModules(listOf(modules)) {
            withInstance<Simple.ComponentA>()
            withParameter<String> { "a_parameter" }
            withProperty("aValue", "string_value")
            withScopeLink(named("scope2"), named("scope1"))
        }
    }

    @Test
    fun `check a module - named dsl value`() {
        val modules = module {
            single { Simple.ComponentB(get(named("_a_"))) }
        }

        koinApplication {
            modules(modules)
            checkModules {
                withInstance<Simple.ComponentA>()
            }
        }
    }

    @Test
    fun `check a scoped module`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    scope(named("scope")) {
                        scoped { Simple.ComponentA() }
                        scoped { Simple.ComponentB(get()) }
                    }
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a scoped module - injected missing`() {
        koinApplication {
            modules(
                module {
                    scope(named("scope")) {
                        scoped { Simple.ComponentB(get()) }
                    }
                }
            )
        }.checkModules {
            withInstance<Simple.ComponentA>()
        }
    }

    @Test
    fun `check a scoped module compact - injected missing`() {
        val m = module {
            scope(named("scope")) {
                scoped { Simple.ComponentB(get()) }
            }
        }

        koinApplication {
            printLogger(Level.DEBUG)
            modules(m)
            checkModules {
                withInstance<Simple.ComponentA>()
            }
        }
    }

    @Test
    fun `check a scoped modules compact - injected missing`() {
        val m1 = module {
            scope(named("scope")) {
                scoped { Simple.ComponentA() }
            }
        }
        val m2 = module {
            scope(named("scope")) {
                scoped { Simple.ComponentB(get()) }
            }
        }

        koinApplication {
            printLogger(Level.DEBUG)
            modules(m1 + m2)
            checkModules {
                withInstance<Simple.ComponentA>()
            }
        }
    }

    @Test
    fun `check a type scope module`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    scope<Simple> {
                        scoped { Simple.ComponentA() }
                        scoped { Simple.ComponentB(get()) }
                    }
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a type scope source module`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    scope<Simple.ComponentA> {
                        scoped { Simple.ComponentB(get()) }
                    }
                }
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
    fun `check a scoped module and ext scope - create scope`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    scope(named("scope2")) {
                        scoped {
                            val a = getScope("scopei1").get<Simple.ComponentA>()
                            Simple.ComponentB(a)
                        }
                    }
                    scope(named("scope1")) {
                        scoped { Simple.ComponentA() }
                    }
                }
            )
        }.checkModules {
            koin.createScope("scopei1", named("scope1"))
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
    fun `check a module with link - checkKoinModules`() {
        val m = module {
            single { Simple.ComponentA() }
            single { Simple.ComponentB(get()) }
        }
        checkKoinModules(listOf(m))
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
            fail("should not pass with broken definitions")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `check a module with params`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { (s: String) -> Simple.MyString(s) }
                    single(UpperCase) { (s: String) -> Simple.MyString(s.uppercase(Locale.getDefault())) }
                }
            )
        }.checkModules {
            withParameters<Simple.MyString> { parametersOf("param") }
            withParameter<Simple.MyString>(UpperCase) { qualifier -> qualifier.toString() }
        }
    }

    @Test
    fun `check a module with params using create method with KClass`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { (s: String) -> Simple.MyString(s) }
                    single(UpperCase) { (s: String) -> Simple.MyString(s.uppercase(Locale.getDefault())) }
                }
            )
        }.checkModules {
            withParameter(Simple.MyString::class) { "param" }
            withParameter(Simple.MyString::class, UpperCase) { qualifier ->
                qualifier.toString()
            }
        }
    }

    @Test
    fun `check a module with params - auto`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { (s: String) -> Simple.MyString(s) }
                    single { (a: Simple.ComponentA) -> Simple.ComponentB(a) }
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a module with params - default value`() {
        val id = "_ID_"
        var injectedValue: String? = null
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { (s: String) ->
                        injectedValue = s
                        Simple.MyString(s)
                    }
                }
            )
        }.checkModules {
            withParameter<Simple.MyString> { id }
        }

        assert(injectedValue == id)
    }

    @Test
    fun `check a module with params - added default value in graph`() {
        val id = "_ID_"
        var _id = ""
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single {
                        _id = get()
                        Simple.MyString(_id)
                    }
                }
            )
        }
        app.checkModules {
            withInstance(id)
        }

        assert(id == _id)
    }

    @Test
    fun `check a module with params - default value in graph`() {
        var _value: String? = null
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single {
                        _value = get()
                        Simple.MyString(_value!!)
                    }
                }
            )
        }
        app.checkModules()

        assert(_value == "")
    }

    @Test
    fun `check a module with complex params - default mocked value in graph`() {
        var _a: Simple.ComponentA? = null
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single {
                        _a = get()
                        Simple.ComponentB(_a!!)
                    }
                }
            )
        }
        app.checkModules {
            withInstance<Simple.ComponentA>()
        }

        assert(_a != null)
    }

    @Test
    fun `check a module with params - default value object`() {
        val a = Simple.ComponentA()
        var injectedValue: Simple.ComponentA? = null
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
        }.checkModules {
            withInstance(a)
        }

        assert(injectedValue == a)
    }

    @Test
    fun `check a module with params - auto scope`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    scope<Simple.ComponentA> {
                        scoped { Simple.ComponentB(get()) }
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
    fun `check a module with property - compact`() {
        val modules = module {
            single { Simple.MyString(getProperty("aValue")) }
        }

        koinApplication {
            printLogger(Level.DEBUG)
            modules(modules)
            checkModules {
                withProperty("aValue", "value")
                withInstance<Simple.ComponentA>()
            }
        }
    }


    @Test
    fun `check a module with linked scopes`() {
        koinApplication {
            printLogger(Level.DEBUG)
            properties(hashMapOf("aValue" to "value"))
            modules(
                module {
                    scope<Simple.ComponentA> {
                        scoped { Simple.ComponentD() }
                    }
                    scope<Simple.ComponentB> {
                        scoped { Simple.ComponentE(get()) }
                    }
                }
            )
        }.checkModules {
            withScopeLink<Simple.ComponentB, Simple.ComponentA>()
        }
    }

    @Test
    fun `check a module with secondary type`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { "the_string" }.bind<CharSequence>()
                    single { 42 } bind Number::class
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a module with secondary types array`() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { "the_string" } binds arrayOf(
                        String::class,
                        CharSequence::class,
                    )
                }
            )
        }.checkModules()
    }

    @Test
    fun `check a module with wrong secondary types array - error`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            koinApplication {
                printLogger(Level.DEBUG)
                modules(
                    module {
                        single { "the_string" } binds arrayOf(
                            CharSequence::class,
                            Int::class,
                        )
                    }
                )
            }.checkModules()
        }

        assertEquals(
            expected = "instance of class kotlin.String is not inheritable from class kotlin.Int",
            actual = exception.message,
        )
    }
}
