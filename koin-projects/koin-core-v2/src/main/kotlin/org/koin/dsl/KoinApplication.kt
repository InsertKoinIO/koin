package org.koin.dsl

import org.koin.core.KoinApplication

typealias KoinAppDeclaration = KoinApplication.() -> Unit

fun koinApplication(appDeclaration: KoinAppDeclaration? = null): KoinApplication {
    val koinApplication = KoinApplication.create()
    appDeclaration?.let { koinApplication.apply(it) }
    return koinApplication
}
