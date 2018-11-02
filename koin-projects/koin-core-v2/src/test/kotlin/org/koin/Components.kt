package org.koin

class Simple {
    class ComponentA()
    class ComponentB(val a: ComponentA)
    class ComponentC(val b: ComponentB)

    interface ComponentInterface1
    interface ComponentInterface2
    class Component1 : ComponentInterface1, ComponentInterface2
    class Component2 : ComponentInterface1
}

class Errors {
    class Boom(){
        init {
            error("Got error while init :(")
        }
    }
    class CycleA(val b : CycleB)
    class CycleB(val a : CycleA)
}