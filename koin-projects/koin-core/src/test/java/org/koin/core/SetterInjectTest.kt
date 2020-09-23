package org.koin.core

import org.junit.Test
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope
import org.koin.core.time.measureDuration
import org.koin.dsl.module
import org.koin.ext.inject

class B : KoinScopeComponent {
    override val scope: Scope by lazy { createScope() }
    override val koin: Koin by lazy { GlobalContext.get() }
}

class C
class D

class BofA(val a: A) : KoinScopeComponent {
    override val scope: Scope by lazy { createScope() }
    override val koin: Koin by lazy { GlobalContext.get() }
}

class CofB(val b: BofA)

class A : KoinScopeComponent {
    override val scope: Scope by lazy { createScope() }
    override val koin: Koin by lazy { GlobalContext.get() }

    lateinit var b: B
    lateinit var c: C
}

@OptIn(KoinApiExtension::class)
class A_inj : KoinComponent {
    val b: B by inject()
    val c: C by inject()
}

class PlayTest {

    @Test
    fun `setter injection`() {
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

        stopKoin()
    }
}