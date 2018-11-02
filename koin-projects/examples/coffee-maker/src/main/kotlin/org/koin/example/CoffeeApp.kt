package org.koin.example

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.dsl.koin
import kotlin.system.measureTimeMillis

class CoffeeApp : KoinComponent {

    val maker: CoffeeMaker by inject()
}

fun main(vararg args: String) {

    koin {
        loadModules(coffeeAppModule)
    }.start()

//    startKoin(
//        list = listOf(),
//        logger = PrintLogger(showDebug = true)
//    )
    val coffeeShop = CoffeeApp()

    val appDuration = measureTimeMillis {
        coffeeShop.maker.brew()
    }

    println("executed in $appDuration ms")
}
