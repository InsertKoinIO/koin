package org.koin.dsl

import org.koin.core.KoinAppDeclaration
import org.koin.core.KoinApplication

fun koin(appDeclaration: KoinAppDeclaration? = null): KoinApplication {
    val koinApplication = KoinApplication.create()
    appDeclaration?.let { koinApplication.apply(it) }
    return koinApplication
}