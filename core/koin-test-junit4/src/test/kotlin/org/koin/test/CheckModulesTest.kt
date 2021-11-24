package org.koin.test

import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.check.checkKoinModules
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito

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
        }

        checkKoinModules(modules){
            withInstance<Simple.ComponentA>()
//            withInstance<String>("a_parameter")
            withParameter<String> { "a_parameter" }
            withProperty("aValue", "string_value")
        }
    }

    @Test
    fun `check a module - named dsl value`() {
        val modules = module {
            single { Simple.ComponentB(get(named("_a_"))) }
        }

        checkKoinModules(modules){
            withInstance<Simple.ComponentA>()
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

        checkKoinModules(m,logLevel = Level.DEBUG){
            withInstance<Simple.ComponentA>()
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

        checkKoinModules(m1+m2,logLevel = Level.DEBUG){
            withInstance<Simple.ComponentA>()
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
        koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { (s: String) -> Simple.MyString(s) }
                    single(UpperCase) { (s: String) -> Simple.MyString(s.toUpperCase()) }
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
                    single(UpperCase) { (s: String) -> Simple.MyString(s.toUpperCase()) }
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

        checkKoinModules(modules){
            withProperty("aValue", "value")
        }
    }
}