package org.koin.test.mock

import org.koin.core.qualifier.Qualifier
import org.koin.mp.PlatformTools
import org.koin.test.KoinTest
import org.koin.test.get

inline fun <reified T : Any> KoinTest.declare(
    qualifier: Qualifier? = null,
    noinline instance: () -> T
): T {
    val koin = PlatformTools.defaultContext().get()
    koin.declare(instance(), qualifier, override = true)
    return get(qualifier)
}