package org.koin.test.mock

import org.koin.core.Koin
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.standalone.StandAloneKoinApplication
import org.koin.ext.getFullName
import org.koin.test.KoinTest
import org.mockito.Mockito.mock

/**
 * Declare & Create a mock in Koin container for given type
 */
inline fun <reified T : Any> KoinTest.declareMock(
    name: String = "",
    noinline stubbing: (T.() -> Unit)? = null
): T {
    val koin = StandAloneKoinApplication.get().koin

    val clazz = T::class
    logger.info("declare mock for '${clazz.getFullName()}'")

    val foundDefinition: BeanDefinition<T> = koin.beanRegistry.findDefinition(name, clazz) as BeanDefinition<T>?
            ?: throw NoBeanDefFoundException("No definition found for name='$name' & class='$clazz'")

    koin.declareMockedDefinition(foundDefinition)

    return koin.applyStub(stubbing)
}

inline fun <reified T : Any> Koin.applyStub(
    noinline stubbing: (T.() -> Unit)?
): T {
    val instance: T = get()
    stubbing?.let { instance.apply(stubbing) }
    return instance
}

inline fun <reified T : Any> Koin.declareMockedDefinition(
    foundDefinition: BeanDefinition<T>
) {
    val definition: BeanDefinition<T> = foundDefinition.cloneForMock()
    beanRegistry.saveDefinition(definition)
}

inline fun <reified T : Any> BeanDefinition<T>.cloneForMock(): BeanDefinition<T> {
    val copy = this.copy()
    copy.secondaryTypes = this.secondaryTypes
    copy.definition = { mock(T::class.java) }
    copy.attributes = this.attributes.copy()
    copy.options = this.options.copy()
    copy.options.override = true
    copy.kind = this.kind
    copy.createInstanceHolder()
    return copy
}
