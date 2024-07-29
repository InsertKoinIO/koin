package org.koin.core

import org.koin.core.component.KoinComponent
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.ext.inject
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.time.measureTime

class B : KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }
}

class C
class D

class BofA(val a: A) : KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }
}

class CofB(val b: BofA)

class A : KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }
    lateinit var b: B
    lateinit var c: C
}

class A_inj : KoinComponent {
    val b: B by inject()
    val c: C by inject()
}

class PlayTest {

    @AfterTest
    fun after(){
        stopKoin()
    }

    @Test
    fun setter_injection() {
        stopKoin()
        val koin = startKoin {
            modules(
                module {
                    single { B() }
                    single { C() }
                },
            )
        }.koin

        val byInjectDuration = measureTime {
            val ai = A_inj()
            ai.b
            ai.c
        }
        println("by inject in $byInjectDuration")

        val propGetDuration = measureTime {
            val a = A()
            a.b = koin.get()
            a.c = koin.get()
        }
        println("prop get in $propGetDuration")

        val propInjectDuration = measureTime {
            val a = A()
            a::b.inject()
            a::c.inject()
        }
        println("prop inject in $propInjectDuration")

        stopKoin()
    }
}