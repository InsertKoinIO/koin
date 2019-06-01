package org.koin.standalone

import org.koin.core.qualifier.named
import org.koin.dsl.module

@JvmField
val koinModule = module {
    single { ComponentA() }
    single { ComponentB(get()) }
    single { ComponentC(get(), get()) }

    scope(named("Session")) {
        scoped { ComponentD(get()) }
    }
}

class ComponentA
class ComponentB(val componentA: ComponentA)
class ComponentD(val componentB: ComponentB)