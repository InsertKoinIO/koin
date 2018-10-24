package org.koin.example

import org.koin.dsl.module.module

val coffeeAppModule = module {
    single { CoffeeMaker(get(), lazy { get<Heater>() }) }
    single<Pump> { Thermosiphon(get()) }
    single<Heater> { ElectricHeater() }
}