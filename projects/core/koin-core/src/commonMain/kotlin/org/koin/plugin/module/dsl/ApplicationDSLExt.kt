package org.koin.plugin.module.dsl

import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import org.koin.mp.KoinPlatformTools

/**
 * Start Koin with modules discovered from @KoinApplication annotated class.
 * The compiler plugin transforms this call to inject modules(Module1().module, ...) based on
 * the @Configuration tags associated with the type parameter T.
 *
 * Usage:
 * ```kotlin
 * @KoinApplication
 * object MyApp
 *
 * startKoin(MyApp::class) {
 *     printLogger()
 * }
 * // Plugin transforms to:
 * // startKoin {
 * //     printLogger()
 * //     modules(MyModule().module, ...)
 * // }
 * ```
 */
public fun <T : Any> startKoin(appDeclaration: org.koin.dsl.KoinAppDeclaration? = null): org.koin.core.KoinApplication {
    // Generate Call: startKoin(T::class().modules,appDeclaration)
    USE_KOIN_COMPILER_PLUGIN("startKoin<T>()")
}

public fun startKoinWith(modules : List<Module>, appDeclaration: org.koin.dsl.KoinAppDeclaration? = null): org.koin.core.KoinApplication {
    return KoinPlatformTools.defaultContext().startKoin {
        modules(modules)
        if (appDeclaration != null) appDeclaration()
    }
}

/**
 * Create KoinApplication with modules discovered from @KoinApplication annotated class.
 * Similar to startKoin but doesn't register globally.
 *
 * Usage:
 * ```kotlin
 * koinApplication(MyApp::class) {
 *     printLogger()
 * }
 * ```
 */
public fun <T : Any> koinApplication(appDeclaration: org.koin.dsl.KoinAppDeclaration? = null): org.koin.core.KoinApplication {
    // Generate Call: koinApplicationWith(T::class().modules,appDeclaration)
    USE_KOIN_COMPILER_PLUGIN("koinApplication<T>()")
}

public fun koinApplicationWith(modules : List<Module>, appDeclaration: org.koin.dsl.KoinAppDeclaration? = null): org.koin.core.KoinApplication {
    return koinApplication {
        modules(modules)
        if (appDeclaration != null) appDeclaration()
    }
}