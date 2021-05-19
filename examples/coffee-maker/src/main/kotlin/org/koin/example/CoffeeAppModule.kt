package org.koin.example

import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.single

val coffeeAppModule = module {
    single { CoffeeMaker(get(), get()) }
    single<Pump> { Thermosiphon(get()) }
    single<Heater> { ElectricHeater() }
}

// val coffeeAppModule = module {
//     single<CoffeeMaker>()
//     single<Thermosiphon>() bind Pump::class
//     single<ElectricHeater>() bind Heater::class
// }