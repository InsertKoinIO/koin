package org.koin.example

import org.koin.dsl.module

val coffeeAppModule = module {
    single { CoffeeMaker(get(), get()) }
    single<Pump> { Thermosiphon(get()) }
    single<Heater> { ElectricHeater() }
}

/*
val coffeeAppModuleExt = module {
    single<CoffeeMaker>()
    singleBy<Pump, Thermosiphon>()
    singleBy<Heater, ElectricHeater>()
}
*/
