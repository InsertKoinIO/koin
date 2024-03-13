package org.koin

import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue

class Simple {
    class ComponentA
    class ComponentB(val a: ComponentA)
    class ComponentC(val b: ComponentB)

    interface ComponentInterface1
    interface ComponentInterface2
    class Component1 : ComponentInterface1, ComponentInterface2
    class Component2 : ComponentInterface1
    class UserComponent(val c1: ComponentInterface1)

    class MySingle(val id: Int)
    class MyTwinSingle(val i1: Int, val i2: Int)
    class MyTwinSingleMix(val i1: Int, val i2: Int, val a: ComponentA)
    class MySingleWithNull(val id: Int?)
    class MySingleAndNull(val a: ComponentA? = null, val ms: MySingle)
    class MyIntFactory(val id: Int)
    class MyStringFactory(val s: String)
    class AllFactory(val ints: MyIntFactory, val strings: MyStringFactory)
    class AllFactory2(val strings: MyStringFactory, val ints: MyIntFactory)
}

class ComplexQualifier(val value1: Int, val value2: String) : Qualifier {
    override val value: QualifierValue = "$value1$value2"
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
