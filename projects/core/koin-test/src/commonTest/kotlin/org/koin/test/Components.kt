package org.koin.test

import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatformTools

@Suppress("unused")
class Simple {
    class ComponentA : MyComponentA
    class ComponentB(val a: ComponentA) : MyComponentB
    interface MyComponentA
    interface MyComponentB
    class ComponentC(val b: MyComponentB)
    class MyString(val s: String)

    class UUIDComponent {
        fun getUUID() = KoinPlatformTools.generateId()
    }

    class CycleAB(val b: CycleBA)
    class CycleBA(val a: CycleAB)
}

object UpperCase : Qualifier {
    override val value: String = "UpperCase"
}
