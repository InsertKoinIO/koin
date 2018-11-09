package org.koin.test

import java.util.*

class Simple {
    class ComponentA
    class UUIDComponent(){
        fun getUUID() = UUID.randomUUID().toString()
    }
}