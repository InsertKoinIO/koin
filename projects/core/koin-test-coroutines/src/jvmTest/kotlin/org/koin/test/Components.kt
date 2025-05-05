package org.koin.test

class Simple {
    class ComponentA
    class ComponentB(val a: ComponentA)
    class ComponentC(val a: ComponentA)
}