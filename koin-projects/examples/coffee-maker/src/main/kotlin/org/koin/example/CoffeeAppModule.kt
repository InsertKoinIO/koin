package org.koin.example

import org.koin.dsl.module.module

val coffeeMakerModule = module {
    //    single { CoffeeMaker(get()) }
//    single { Thermosiphon(get()) as Pump }
//    single { ElectricHeater() as Heater }

    single<CoffeeMaker>()
    singleOf<Thermosiphon, Pump>()
    singleOf<ElectricHeater, Heater>()
}