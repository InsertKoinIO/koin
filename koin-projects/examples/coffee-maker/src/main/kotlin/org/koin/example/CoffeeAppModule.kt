package org.koin.example

import org.koin.dsl.module.module

val coffeeMakerModule = module {
    single<CoffeeMaker>()
    single<Pump> { create<Thermosiphon>() }
    single<Heater> { create<ElectricHeater>() }
}