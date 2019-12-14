package org.koin.core

import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.scope.Scope
import org.koin.core.time.measureDuration
import org.koin.dsl.module
import kotlin.reflect.KMutableProperty0

class B
class C

class A {
    lateinit var b: B
    lateinit var c: C
}

class A_inj : KoinComponent {
    val b: B by inject()
    val c: C by inject()
}

class PlayTest {

    @Test
    fun `play setter injection`() {
        val koin = startKoin {
            modules(module {
                single { B() }
                single { C() }
            })
        }.koin


        measureDuration("by inject") {
            val ai = A_inj()
            ai.b
            ai.c
        }

        measureDuration("prop get") {
            val a = A()
            a.b = koin.get()
            a.c = koin.get()
        }

        measureDuration("prop inject") {
            val a = A()
            a::b.inject()
            a::c.inject()
        }
    }

}

inline fun <reified T> KMutableProperty0<T>.inject() {
    set(GlobalContext.get().get())
}

//TODO real interest here?
inline fun <reified T> KMutableProperty0<T>.inject(koin: Koin) {
    set(koin.get())
}
inline fun <reified T> KMutableProperty0<T>.inject(scope: Scope) {
    set(scope.get())
}

//TODO InjectAll - Reflection