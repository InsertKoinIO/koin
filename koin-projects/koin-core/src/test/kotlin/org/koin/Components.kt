

package org.koin

class Simple {
    class ComponentA
    class ComponentB(val a: ComponentA)
    class ComponentC(val b: ComponentB)

    interface ComponentInterface1
    interface ComponentInterface2
    class Component1 : ComponentInterface1, ComponentInterface2
    class Component2 : ComponentInterface1
    class UserComponent(val c1 : ComponentInterface1)

    class MySingle(val id: Int)
    class MyIntFactory(val id: Int)
    class MyStringFactory(val s: String)
    class AllFactory(val ints: MyIntFactory, val strings: MyStringFactory)
}

@Suppress("unused")
class Errors {
    class Boom {
        init {
            error("Got error while init :(")
        }
    }
    class CycleA(val b: CycleB)
    class CycleB(val a: CycleA)
}