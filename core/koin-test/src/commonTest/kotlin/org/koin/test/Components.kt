package org.koin.test

import org.koin.core.qualifier.Qualifier
import org.koin.mp.PlatformTools

@Suppress("unused")
class Simple {
    class ComponentA
    class ComponentB(val a: ComponentA)
    class ComponentC(val b: ComponentB)
    class MyString(val s: String)

    class UUIDComponent {
        fun getUUID() = PlatformTools.generateId()
    }
}

object UpperCase : Qualifier {
    override val value: String = "UpperCase"

}
