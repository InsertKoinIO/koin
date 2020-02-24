package org.koin.example

import org.koin.dsl.module
import org.koin.experimental.builder.single
import org.koin.experimental.builder.singleBy

val coffeeAppModule = module {
    single { CoffeeMaker(get(), get()) }
    single<Pump> { Thermosiphon(get()) }
    single<Heater> { ElectricHeater() }
}

val coffeeAppModuleExt = module {
    single<CoffeeMaker>()
    singleBy<Pump, Thermosiphon>()
    singleBy<Heater, ElectricHeater>()
}