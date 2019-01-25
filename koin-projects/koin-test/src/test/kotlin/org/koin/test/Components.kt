package org.koin.test

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