package org.koin.test.verify

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.ModuleConfiguration
import kotlin.reflect.KClass


@OptIn(KoinInternalApi::class)
@KoinExperimentalAPI
fun ModuleConfiguration.verify(extraTypes: List<KClass<*>> = emptyList(), injections: List<ParameterTypeInjection>? = emptyList()){
    println("Verifying Module Configuration - Modules '$this' ...")
    // Verify list of modules
    val modulesVerification : Verification = _modules.map { Verification(it, extraTypes, injections) }.fold(Verification(extraTypes = extraTypes, injections = injections)) { r,v -> r+v }
    modulesVerification.verify()
    println("Verifying Module Configuration - Lazy Modules '$this' ...")
    // Verify list of lazy modules
    val lazyModulesVerification : Verification =_lazyModules.map { Verification(it.value, extraTypes, injections) }.fold(modulesVerification) { r,v -> r+v }
    lazyModulesVerification.verify()
}