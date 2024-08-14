package org.koin.test.mock

import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatformTools
import org.koin.test.KoinTest
import org.koin.test.get

inline fun <reified T : Any> KoinTest.declare(
    qualifier: Qualifier? = null,
    noinline instance: () -> T,
): T {
    val koin = KoinPlatformTools.defaultContext().get()
    koin.declare(instance(), qualifier, allowOverride = true)
    return get(qualifier)
}
