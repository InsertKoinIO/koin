package org.koin.example

import org.koin.dsl.module

val coffeeAppModule = module {
    single() { CoffeeMaker(get(), get()) }
    single<Pump> { Thermosiphon(get()) }
    single<Heater> { ElectricHeater() }
}

val testModule = module {

}

val invalidModule = Module()
val dummy = Dummy()

class Module
class Dummy