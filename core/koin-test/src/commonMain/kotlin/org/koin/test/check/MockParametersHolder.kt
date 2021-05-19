package org.koin.test.check

import org.koin.core.parameter.ParametersHolder
import org.koin.mp.KoinPlatformTools
import org.koin.test.mock.MockProvider
import kotlin.reflect.KClass

class MockParametersHolder(
    val defaultValues: Map<KClass<*>, Any> = hashMapOf(),
    val allowedMocks: List<KClass<*>>
) : ParametersHolder() {
    override fun <T> elementAt(i: Int, clazz: KClass<*>): T {
        return getOrNull(clazz) as? T ?: error("Can't get parameter mock for class: $clazz")
    }

    override fun <T> getOrNull(clazz: KClass<*>): T? {
        return defaultValues[clazz] as? T ?: if (clazz.isPrimaryType()) defaultPrimitiveValue(clazz) else autoMocks<T>(
            clazz
        )
    }

    private fun <T> autoMocks(clazz: KClass<*>): T? {
        return if (clazz in allowedMocks) {
            KoinPlatformTools.defaultContext().get().logger.error("|- Parameter Mock -> $clazz")
            return MockProvider.makeMock(clazz) as? T
        } else null
    }

    private fun <T> defaultPrimitiveValue(clazz: KClass<*>): T? {
        val value = when (clazz) {
            Int::class -> 0
            Double::class -> 0.0
            Float::class -> 0.0f
            Long::class -> 0L
            String::class -> ""
            else -> null
        } as T?
        value?.let { KoinPlatformTools.defaultContext().get().logger.error("|- default value -> '$value'") }
        return value
    }
}

private fun KClass<*>.isPrimaryType(): Boolean {
    return this in primaryTypes
}

val primaryTypes = listOf<KClass<*>>(
    Int::class,
    Double::class,
    Float::class,
    Long::class,
    String::class
)