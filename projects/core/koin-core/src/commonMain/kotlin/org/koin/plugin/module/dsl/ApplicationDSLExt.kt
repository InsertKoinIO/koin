package org.koin.plugin.module.dsl

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.includes
import org.koin.dsl.koinApplication
import org.koin.dsl.koinConfiguration
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
 * startKoin<MyApp>() {
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

/**
 * Compiler Plugin Support Function - Start Koin with the provided list of modules.
 * This function is called by the compiler plugin after transforming startKoin<T>().
 *
 * @param modules List of modules discovered from @KoinApplication annotated class
 * @param appDeclaration Optional Koin application configuration block
 * @return The configured KoinApplication instance
 */
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
 * koinApplication<MyApp>() {
 *     printLogger()
 * }
 * ```
 */
public fun <T : Any> koinApplication(appDeclaration: org.koin.dsl.KoinAppDeclaration? = null): org.koin.core.KoinApplication {
    // Generate Call: koinApplicationWith(T::class().modules,appDeclaration)
    USE_KOIN_COMPILER_PLUGIN("koinApplication<T>()")
}

/**
 * Compiler Plugin Support Function - Create KoinApplication with the provided list of modules.
 * This function is called by the compiler plugin after transforming koinApplication<T>().
 *
 * @param modules List of modules discovered from @KoinApplication annotated class
 * @param appDeclaration Optional Koin application configuration block
 * @return The configured KoinApplication instance (not registered globally)
 */
public fun koinApplicationWith(modules : List<Module>, appDeclaration: org.koin.dsl.KoinAppDeclaration? = null): org.koin.core.KoinApplication {
    return koinApplication {
        modules(modules)
        if (appDeclaration != null) appDeclaration()
    }
}


/**
 * Compiler Plugin Stub - Create KoinConfiguration with modules discovered from @KoinApplication annotated class.
 * The compiler plugin transforms this call to inject modules based on the @Configuration tags associated with type parameter T.
 *
 * Usage:
 * ```kotlin
 * koinConfiguration<MyApp>() {
 *     printLogger()
 * }
 * ```
 *
 * @param T Type annotated with @KoinApplication
 * @param appDeclaration Optional Koin application configuration block
 * @return KoinConfiguration with discovered modules
 */
public fun <T : Any> koinConfiguration(appDeclaration: org.koin.dsl.KoinAppDeclaration? = null) : KoinConfiguration {
    // Generate Call: koinConfigurationWith(T::class().modules)
    USE_KOIN_COMPILER_PLUGIN("koinConfiguration<T>()")
}


/**
 * Compiler Plugin Support Function - Create KoinConfiguration with the provided list of modules.
 * This function is called by the compiler plugin after transforming koinConfiguration<T>().
 *
 * @param modules List of modules discovered from @KoinApplication annotated class
 * @param appDeclaration Optional Koin application configuration block
 * @return KoinConfiguration with provided modules
 */
public fun koinConfigurationWith(modules : List<Module>, appDeclaration: org.koin.dsl.KoinAppDeclaration? = null) : KoinConfiguration = koinConfiguration {
    includes(appDeclaration)
    modules(modules)
}


/**
 * Compiler Plugin Stub - Apply configuration with modules discovered from @KoinApplication annotated class to an existing KoinApplication.
 * The compiler plugin transforms this call to inject modules based on the @Configuration tags associated with type parameter T.
 *
 * Usage:
 * ```kotlin
 * startKoin {
 *     printLogger()
 * }.withConfiguration<MyApp>()
 * ```
 *
 * @param T Type annotated with @KoinApplication
 * @param appDeclaration Optional Koin application configuration block
 */
public fun <T : Any> KoinApplication.withConfiguration(appDeclaration: org.koin.dsl.KoinAppDeclaration? = null) {
    // Generate Call: koinConfigurationWith(T::class().modules)
    USE_KOIN_COMPILER_PLUGIN("KoinApplication.useKoinConfiguration<T>()")
}

/**
 * Compiler Plugin Support Function - Apply configuration with the provided list of modules to an existing KoinApplication.
 * This function is called by the compiler plugin after transforming withConfiguration<T>().
 *
 * @param modules List of modules discovered from @KoinApplication annotated class
 * @param appDeclaration Optional Koin application configuration block
 */
public fun KoinApplication.withConfigurationWith(modules : List<Module>, appDeclaration: org.koin.dsl.KoinAppDeclaration? = null) {
    includes(appDeclaration)
    modules(modules)
}