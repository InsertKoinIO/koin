package org.koin.test.android

import org.koin.dsl.module

val ModuleA = module {
    includes(ModuleB)
    factory(qualifierA) { Person(parent = null) }
}