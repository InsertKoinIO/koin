package org.koin.core

import org.koin.Simple
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.get
import org.koin.core.component.getOrCreateScope
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.assertDefinitionsCount
import kotlin.test.Test
import kotlin.test.assertEquals

class MultipleModuleDeclarationTest {

    @Test
    fun `run with DI with several modules`() {
        val app = koinApplication {
            modules(
                listOf(
                    module {
                        single { Simple.ComponentA() }
                    },
                    module {
                        single { Simple.ComponentB(get()) }
                    },
                ),
            )
        }

        app.assertDefinitionsCount(2)
    }

    @Test
    fun `resolve DI with several modules`() {
        val app = koinApplication {
            modules(
                listOf(
                    module {
                        single { Simple.ComponentA() }
                    },
                    module {
                        single { Simple.ComponentB(get()) }
                    },
                ),
            )
        }

        val koin = app.koin
        val a = koin.get<Simple.ComponentA>()
        val b = koin.get<Simple.ComponentB>()

        assertEquals(a, b.a)
    }

    @Test
    fun `resolve DI with several modules with override contract`() {
        val expected = TestDependency(
            name = "expected",
        )

        val app = koinApplication {
            modules(
                module {
                    single {
                        TestDependency(
                            name = "a",
                        )
                    }
                },
                module {
                    single {
                        TestDependency(
                            name = "b",
                        )
                    }
                },
                module {
                    single {
                        TestDependency(
                            name = "c",
                        )
                    }
                },
                module {
                    single {
                        expected
                    }
                },
            )
        }

        val koin = app.koin
        val actual = koin.get<TestDependency>()

        assertEquals(expected, actual)
    }

    @Test
    fun `resolve DI with several nested modules with override contract`() {
        val expected = TestDependency(
            name = "expected",
        )

        val app = koinApplication {
            modules(
                module {
                    includes(
                        module {
                            TestDependency(
                                name = "a",
                            )
                        },
                    )
                    single {
                        TestDependency(
                            name = "a",
                        )
                    }
                },
                module {
                    single {
                        TestDependency(
                            name = "b",
                        )
                    }
                },
                module {
                    single {
                        TestDependency(
                            name = "c",
                        )
                    }
                },
                module {
                    single {
                        expected
                    }
                },
            )
        }

        val koin = app.koin
        val actual = koin.get<TestDependency>()

        assertEquals(expected, actual)
    }

    @Test
    fun `resolve DI with several nested modules from scope with override contract`() {
        val expected = TestDependency(
            name = "expected",
        )

        val app = koinApplication {
            modules(
                module {
                    includes(
                        module {
                            TestDependency(
                                name = "a",
                            )
                        },
                    )
                    single {
                        TestDependency(
                            name = "a",
                        )
                    }
                },
                module {
                    single {
                        TestDependency(
                            name = "b",
                        )
                    }
                },
                module {
                    single {
                        TestDependency(
                            name = "c",
                        )
                    }
                },
                module {
                    single {
                        expected
                    }
                },
            )
        }

        val koin = app.koin
        val scope = ModuleTestScope(koin)
        val actual = scope.get<TestDependency>()

        assertEquals(expected, actual)
    }

    @Test
    fun `resolve DI with several deeply nested modules from scope with override contract`() {
        val expected = TestDependency(
            name = "expected",
        )

        val app = koinApplication {
            modules(
                module {
                    includes(
                        module {
                            includes(
                                module {
                                    TestDependency(
                                        name = "a",
                                    )
                                },
                            )
                            single {
                                TestDependency(
                                    name = "a",
                                )
                            }
                        },
                        module {
                            single {
                                TestDependency(
                                    name = "b",
                                )
                            }
                        },
                        module {
                            single {
                                TestDependency(
                                    name = "c",
                                )
                            }
                        },
                        module {
                            single {
                                expected
                            }
                        },
                    )
                },
            )
        }

        val koin = app.koin
        val scope = ModuleTestScope(koin)
        val actual = scope.get<TestDependency>()

        assertEquals(expected, actual)
    }

    data class TestDependency(
        val name: String,
    )

    class ModuleTestScope(
        private val koin: Koin,
    ) : KoinScopeComponent {
        override val scope: Scope by getOrCreateScope()

        override fun getKoin(): Koin =
            koin
    }
}
