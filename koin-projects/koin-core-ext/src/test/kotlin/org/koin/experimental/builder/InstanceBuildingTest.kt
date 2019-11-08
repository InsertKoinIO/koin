package org.koin.experimental.builder

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.core.time.measureDurationForResult
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

class InstanceBuilderTest {

    class MyComponent

//    @Test
//    fun `should create an instance for class`() {
//        val (ctor, ctorDuration) = measureDurationForResult {
//            MyComponent::class.getFirstJavaConstructor()
//        }
//        println("got ctor in $ctorDuration ms")
//
//        val (instance, instanceDuration) = measureDurationForResult {
//            ctor.makeInstance<MyComponent>(emptyArray())
//        }
//        assertNotNull(instance)
//
//        println("instance created in $instanceDuration ms")
//    }

    @Test
    fun `should create an instance for kclass`() {
        val (ctor, ctorDuration) = measureDurationForResult {
            MyComponent::class.getFirstConstructor()
        }
        println("got ctor in $ctorDuration ms")

        val (instance, instanceDuration) = measureDurationForResult {
            ctor.call() as MyComponent
        }
        assertNotNull(instance)

        println("instance created in $instanceDuration ms")
    }
}

fun KClass<*>.getFirstConstructor(): KFunction<*> {
    return constructors.firstOrNull() ?: error("No constructor found for class '$this'")
}
