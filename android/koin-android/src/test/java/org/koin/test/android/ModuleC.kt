package org.koin.test.android

import org.koin.dsl.module

val ModuleC = module {
    factory(qualifierC) { Person(parent = get(qualifierB)) }
}