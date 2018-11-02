package org.koin

class Simple {
    class ComponentA()
    class ComponentB(val a: ComponentA)
    class ComponentC(val b: ComponentB)

    interface ComponentInterface
    class Component1 : ComponentInterface
    class Component2 : ComponentInterface
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