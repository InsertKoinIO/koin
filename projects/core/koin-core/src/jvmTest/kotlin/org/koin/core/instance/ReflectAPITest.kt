package org.koin.core.instance

import org.junit.Test
import kotlin.reflect.KClass
import kotlin.time.measureTimedValue

class ReflectAPITest {

    @Test
    fun `reflect api to construct an instance`() {
        measureRefInstantiation(ComponentA::class)

        measureFctInstantiation { ComponentA() }

        measureRefInstantiation(ComponentA2::class)

        measureFctInstantiation { ComponentA2() }

        measureRefInstantiation(ComponentB::class)

        measureFctInstantiation { ComponentB(measureFctInstantiation { ComponentA() }) }
    }

    private inline fun <reified T : Any> measureFctInstantiation(noinline code: () -> T): T {
        val (instance, duration) = measureTimedValue(code)
        println("+ make fct - ${T::class} in $duration")
        return instance
    }

    private inline fun <reified T : Any> measureRefInstantiation(clazz: KClass<T>): T {
        val (instance, duration) = measureTimedValue {
            val javaClass = clazz.java
            val ctor = javaClass.constructors.first()
            val types = ctor.parameterTypes
            return@measureTimedValue if (types.isEmpty()) {
                ctor.newInstance() as T
            } else {
                println("|- types: ${types.toList()}")
                val (map, makeSubInstancesDuration) = measureTimedValue {
                    types.map { it.constructors.first().newInstance() }
                }
                println("|- make sub instances: $map in $makeSubInstancesDuration")
                val toTypedArray = map.toTypedArray()
                val (instance, makeInstanceDuration) = measureTimedValue {
                    ctor.newInstance(*toTypedArray) as T
                }
                println("|- created with subtypes: $map in $makeInstanceDuration")
                return@measureTimedValue instance
            }
        }

        println("+ make ref - ${T::class} in $duration")
        return instance
    }
}
