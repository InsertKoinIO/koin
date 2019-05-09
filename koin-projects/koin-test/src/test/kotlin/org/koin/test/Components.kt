package org.koin.test

import org.koin.core.qualifier.Qualifier
import java.util.*

@Suppress("unused")
class Simple {
    class ComponentA
    class ComponentB(val a: ComponentA)
    class MyString(val s: String)

    class UUIDComponent {
        fun getUUID() = UUID.randomUUID().toString()
    }
}

object UpperCase : Qualifier
