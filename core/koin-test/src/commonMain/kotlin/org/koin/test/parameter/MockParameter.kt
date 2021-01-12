package org.koin.test.parameter

import org.koin.core.parameter.DefinitionParameters
import org.koin.core.scope.Scope
import org.koin.mp.PlatformTools
import org.koin.test.mock.MockProvider
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class MockParameter(
    private val scope: Scope,
    private val defaultValues: MutableMap<String, Any>
) : DefinitionParameters(arrayListOf()) {
    override fun <T> elementAt(i: Int, clazz: KClass<*>): T {
        return defaultValues[PlatformTools.getClassName(clazz)] as? T
            ?: getDefaultPrimaryValue(clazz)
            ?: (MockProvider.makeMock(clazz)) as T
    }

    private fun <T> getDefaultPrimaryValue(clazz: KClass<*>): T? {
        return when (PlatformTools.getClassName(clazz)) {
            PlatformTools.getClassName(String::class) -> "" as T
            PlatformTools.getClassName(Int::class) -> 0 as T
            PlatformTools.getClassName(Double::class) -> 0.0 as T
            PlatformTools.getClassName(Float::class) -> 0.0f as T
            else -> null
        }
    }

    override fun <T : Any> getOrNull(clazz: KClass<T>): T? {
        return defaultValues.values.firstOrNull { it::class == clazz } as? T
            ?: getDefaultPrimaryValue(clazz) //?: elementAt(0, clazz)
    }
}
