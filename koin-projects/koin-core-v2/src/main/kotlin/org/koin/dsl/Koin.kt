package org.koin.dsl

import org.koin.core.Koin
import org.koin.core.KoinAppDeclaration


fun koin(appDeclaration: KoinAppDeclaration? = null): Koin {
    val instance = Koin.create()
    appDeclaration?.let { instance.apply(it) }
    return instance
}