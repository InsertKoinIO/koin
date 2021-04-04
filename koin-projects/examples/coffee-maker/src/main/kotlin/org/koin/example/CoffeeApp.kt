package org.koin.example

import jdk.nashorn.internal.ir.annotations.Ignore
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.bind
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.time.measureDuration
import org.koin.ksp.KoinInject

@OptIn(KoinApiExtension::class)
class CoffeeApp : KoinComponent {


    val maker: CoffeeMaker by inject()

    @KoinInject
    lateinit var maker2: CoffeeMaker

    val aaaa = "aaa"

    val str: String = getKoin().get<String>()

    @Ignore

    val l by lazy { "lazy" }
    fun method(b: Boolean): Int = 3

    init {
        maker2 = getKoin().get() //bind<CoffeeMaker>()
    }
}

fun main() {
    startKoin {
        printLogger()
        modules(listOf(coffeeAppModule))
    }

    val coffeeShop = CoffeeApp()
    measureDuration("Got Coffee") {
        coffeeShop.maker.brew()
    }
    stopKoin()
}