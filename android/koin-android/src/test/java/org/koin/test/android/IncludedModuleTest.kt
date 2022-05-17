package org.koin.test.android

import org.junit.Test
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication

class Person(val parent: Person?)

val qualifierA = named("a")
val qualifierB = named("b")
val qualifierC = named("c")

class IncludedModuleTest {
    @Test
    fun `shouldLoadIncludedModules`() {
        val koin = koinApplication { modules(ModuleA) }.koin

        // Assert that all included modules are in fact included by retrieving its dependencies.
        requireNotNull(koin.get<Person>(qualifierA))
        requireNotNull(koin.get<Person>(qualifierB))
        requireNotNull(koin.get<Person>(qualifierC))
    }
}
