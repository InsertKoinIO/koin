package org.koin.standalone

import org.koin.dsl.module.module

@JvmField
val koinModule = module {
    single { ComponentA() }
    single { ComponentB(get()) }
    single { ComponentC(get(), get()) }

    module("anotherModule") {
        scope { ComponentD(get()) }
    }
}

class ComponentA()
class ComponentB(val componentA: ComponentA)
class ComponentD(val componentB: ComponentB)