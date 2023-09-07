package org.koin.test.parameter

import org.koin.core.parameter.ParametersHolder
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatformTools
import org.koin.test.mock.MockProvider
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class MockParameter(
    private val scope: Scope,
    private val defaultValues: MutableMap<String, Any>,
) : ParametersHolder(arrayListOf()) {
    override fun <T> elementAt(i: Int, clazz: KClass<*>): T {
        return defaultValues[KoinPlatformTools.getClassName(clazz)] as? T
            ?: getDefaultPrimaryValue(clazz)
            ?: (MockProvider.makeMock(clazz)) as T
    }

    private fun <T> getDefaultPrimaryValue(clazz: KClass<*>): T? {
        return when (KoinPlatformTools.getClassName(clazz)) {
            KoinPlatformTools.getClassName(String::class) -> "" as T
            KoinPlatformTools.getClassName(Int::class) -> 0 as T
            KoinPlatformTools.getClassName(Double::class) -> 0.0 as T
            KoinPlatformTools.getClassName(Float::class) -> 0.0f as T
            else -> null
        }
    }

    override fun <T> getOrNull(clazz: KClass<*>): T? {
        return defaultValues.values.firstOrNull { clazz.isInstance(it) } as? T
            ?: getDefaultPrimaryValue(clazz) // ?: elementAt(0, clazz)
    }
}
