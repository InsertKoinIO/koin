package org.koin.test.parameter

import org.koin.core.parameter.DefinitionParameters
import org.koin.core.scope.Scope
import org.koin.test.mock.MockProvider
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class MockParameter(
    private val scope: Scope,
    private val defaultValues: MutableMap<String, Any>
) : DefinitionParameters(null) {
    override fun <T> elementAt(i: Int, clazz: KClass<*>): T {
        return defaultValues[clazz.simpleName] as? T ?: when (clazz.simpleName) {
            String::class.java.simpleName -> "" as T
            Int::class.java.simpleName -> 0 as T
            Double::class.java.simpleName -> 0.0 as T
            Float::class.java.simpleName -> 0.0f as T
            else -> {
                (scope.getOrNull<T?>(clazz = clazz) ?: MockProvider.makeMock(clazz)) as T
            }
        }
    }
}
