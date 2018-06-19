package org.koin.example

import org.koin.dsl.module.module

val coffeeMakerModule = module {
    single { CoffeeMaker(get()) }
    single { ElectricHeater() as Heater }
    single { Thermosiphon(get()) as Pump }
}