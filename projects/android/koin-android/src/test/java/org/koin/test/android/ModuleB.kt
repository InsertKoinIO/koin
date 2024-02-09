package org.koin.test.android

import org.koin.dsl.module

val ModuleB = module {
    includes(ModuleC)
    factory(qualifierB) { Person(parent = get(qualifierA)) }
}