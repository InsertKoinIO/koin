package org.koin.resolution

import org.koin.Simple
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.instance.ResolutionContext
import org.koin.core.logger.Level
import org.koin.core.resolution.ResolutionExtension
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.to

@OptIn(KoinInternalApi::class)
class ResolutionExtensionTest {

    @Test
    fun add_extend_resolution(){
        val resolutionExtension = object : ResolutionExtension {
            override val name: String = "hello-null-extension"
            override fun resolve(
                scope: Scope,
                instanceContext: ResolutionContext
            ): Any? {
                return null
            }
        }

        val koin = koinApplication{
            koin.resolver.addResolutionExtension(resolutionExtension)
        }.koin

        assertEquals(1,koin.resolver.extendedResolution.size)
    }

    @Test
    fun extend_resolution_test(){
        val resolutionExtension = object : ResolutionExtension {
            val instanceMap = mapOf<KClass<*>, Any>(
                Simple.ComponentA::class to Simple.ComponentA()
            )

            override val name: String = "hello-extension"
            override fun resolve(
                scope: Scope,
                instanceContext: ResolutionContext
            ): Any? {
                return instanceMap[instanceContext.clazz]
            }
        }

        val koin = koinApplication{
            printLogger(Level.DEBUG)
            koin.resolver.addResolutionExtension(resolutionExtension)
            modules(module {
                single { Simple.ComponentB(get())}
            })
        }.koin

        assertEquals(resolutionExtension.instanceMap[Simple.ComponentA::class], koin.get<Simple.ComponentB>().a)
        assertEquals(1,koin.instanceRegistry.instances.values.size)
    }
}