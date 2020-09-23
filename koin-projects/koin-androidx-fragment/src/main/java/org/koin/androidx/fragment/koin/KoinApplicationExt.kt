package org.koin.androidx.fragment.koin

import androidx.fragment.app.FragmentFactory
import org.koin.androidx.fragment.android.KoinFragmentFactory
import org.koin.core.KoinApplication
import org.koin.core.KoinExperimentalAPI
import org.koin.dsl.module

private val fragmentFactoryModule = module {
    single<FragmentFactory> { KoinFragmentFactory() }
}

/**
 * Setup the KoinFragmentFactory instance
 */
@KoinExperimentalAPI
fun KoinApplication.fragmentFactory() {
    koin.loadModules(listOf(fragmentFactoryModule))
}