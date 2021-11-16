package org.koin.test.android

import org.junit.Test
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class IncludedModuleTest {

    private class Person(parent: Person?)

    private val QualifierA = named("a")
    private val QualifierB = named("b")
    private val QualifierC = named("c")
    private val QualifierD = named("d")

    private val ModuleA = module {
        includes(ModuleB)
        factory(QualifierA) { Person(parent = null) }
    }

    private val ModuleB = module {
        includes(ModuleC)
        factory(QualifierB) { Person(parent = get(QualifierA)) }
    }

    private val ModuleC = module {
        factory(QualifierC) { Person(parent = get(QualifierB)) }
    }

    private val ModuleD = module {
        factory(QualifierD) { Person(parent = get(QualifierA)) }
    }

    @Test(expected = Test.None::class)
    fun `shouldLoadIncludedModules`() {
        val koin = koinApplication { modules(ModuleA) }.koin

        // Assert that all included modules are in fact included by retrieving its dependencies.
        requireNotNull(koin.get<Person>(QualifierA))
        requireNotNull(koin.get<Person>(QualifierB))
        requireNotNull(koin.get<Person>(QualifierC))
        requireNotNull(koin.get<Person>(QualifierD))
    }
}
