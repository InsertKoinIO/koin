package org.koin.example

import org.koin.dsl.module.module

val coffeeMakerModule = module {
    single { CoffeeMaker(get()) }
    single<Pump> { Thermosiphon(get()) }
    single<Heater> { ElectricHeater() }
}