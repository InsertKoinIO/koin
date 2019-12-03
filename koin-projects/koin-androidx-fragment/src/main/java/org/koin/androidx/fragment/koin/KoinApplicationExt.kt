package org.koin.androidx.fragment.koin

import androidx.fragment.app.FragmentFactory
import org.koin.androidx.fragment.android.KoinFragmentFactory
import org.koin.core.KoinApplication
import org.koin.dsl.module

/**
 * Setup the KoinFragmentFactory instance
 */
fun KoinApplication.fragmentFactory() {
    koin.loadModules(listOf(module {
        single<FragmentFactory> { KoinFragmentFactory() }
    }))
}