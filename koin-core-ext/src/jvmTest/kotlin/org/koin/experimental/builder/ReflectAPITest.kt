package org.koin.experimental.builder

import org.junit.Test
import org.koin.core.time.measureDurationForResult
import kotlin.reflect.KClass

class ReflectAPITest {

    @Test
    fun `reflect api to construct an instance`() {
        makeJavaInstance(ComponentA::class)

        makeInstance { ComponentA() }

        makeJavaInstance(ComponentA2::class)

        makeInstance { ComponentA2() }

        makeJavaInstance(ComponentB::class)

        makeInstance { ComponentB(makeInstance { ComponentA() }) }
    }

    inline fun <reified T : Any> makeInstance(noinline code: () -> T): T {
        return measure("+ make fct - ${T::class}", code)
    }

    private inline fun <reified T : Any> makeJavaInstance(clazz: KClass<T>): T {
        return measure("+ make ref - ${T::class}") {
            val javaClass = clazz.java
            val ctor = javaClass.constructors.first()
            val types = ctor.parameterTypes
            return@measure if (types.isEmpty()) {
                ctor.newInstance() as T
            } else {
                println("!- types:${types.toList()}")
                val (map, duration) = measureDurationForResult {
                    types.map { it.constructors.first().newInstance() }
                }
                println("!- make sub instances:$map in $duration")
                val toTypedArray = map.toTypedArray()
                measure("!- created with subtypes") {
                    ctor.newInstance(*toTypedArray) as T
                }
            }
        }
    }


}

fun <T : Any> measure(message: String, code: () -> T): T {
    val (i, time) = measureDurationForResult(code)
    println("$message in $time ms")
    return i
}