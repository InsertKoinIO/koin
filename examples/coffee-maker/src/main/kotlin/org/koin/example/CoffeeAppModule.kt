package org.koin.example

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

//val coffeeAppModule = module {
//    single { CoffeeMaker(get(), get()) }
//    single<Pump> { Thermosiphon(get()) }
//    single<Heater> { ElectricHeater() }
//}

val coffeeAppModule = module {
    singleOf(::CoffeeMaker)
    singleOf(::Thermosiphon) { bind<Pump>() }
    singleOf(::ElectricHeater) { bind<Heater>() }
}