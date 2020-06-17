package org.koin.test.parameter

import org.koin.core.parameter.DefinitionParameters
import org.koin.core.scope.Scope
import org.mockito.Mockito
import kotlin.reflect.KClass

class MockParameter(val scope: Scope) : DefinitionParameters(null) {
    override fun <T> elementAt(i: Int, clazz: KClass<*>): T {
        return when (clazz.simpleName) {
            String::class.java.simpleName -> "" as T
            Int::class.java.simpleName -> 0 as T
            Double::class.java.simpleName -> 0.0 as T
            Float::class.java.simpleName -> 0.0f as T
            else -> {
                val found = try { scope.getOrNull<T?>(clazz = clazz) } catch (e: Exception) { }
                (found ?: Mockito.mock(clazz.java)) as T
            }
        }
    }
}
