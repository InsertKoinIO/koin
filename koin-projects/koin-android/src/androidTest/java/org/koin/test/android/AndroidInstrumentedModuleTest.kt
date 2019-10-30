package org.koin.test.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.mock.declareMock
import org.mockito.Mockito

/**
 * Android Module Tests
 */
class AndroidInstrumentedModuleTest : KoinTest {

    private val sampleModule = module {
        single { AndroidComponentA(androidContext()) }
        single { AndroidComponentB(get()) }
        single<Service> { SomeService() }
        single<IMyService>(named("myService")) { SomeMyService() }
        single<IMyService>(named("myService2")) { SomeMyService2() }
    }

    class AndroidComponentA(val androidContext: Context)
    class AndroidComponentB(val androidComponent: AndroidComponentA)
    interface Service
    class SomeService : Service

    interface IMyService
    class SomeMyService : IMyService

    interface IMyService2 : IMyService
    class SomeMyService2 : IMyService2

    @Test
    fun shouldInjectWithInstrumentedContext() {
        val testContext = InstrumentationRegistry.getInstrumentation().context

        val koin = koinApplication {
            androidContext(testContext)
            modules(sampleModule)
        }.koin

        koin.get<AndroidComponentA>()
    }

    @Test
    fun shouldInjectWithMockedContext() {
        val testContext = Mockito.mock(Context::class.java)

        val koin = koinApplication {
            androidContext(testContext)
            modules(sampleModule)
        }.koin

        val a = koin.get<AndroidComponentA>()
        val b = koin.get<AndroidComponentB>()

        assertEquals(a, b.androidComponent)
    }

    @Test
    fun shouldInjectAdHocDeclaredDependency() {
        val testContext = Mockito.mock(Context::class.java)

        startKoin {
            androidContext(testContext)
            modules(sampleModule)
        }
        val mockedService = declareMock<Service>()

        val injectedService = get<Service>()

        assertEquals(mockedService, injectedService)

        stopKoin()
    }

    @Test
    fun shouldInjectAdHocDeclaredWithQualifierDependency() {
        val testContext = Mockito.mock(Context::class.java)

        startKoin {
            androidContext(testContext)
            modules(sampleModule)
        }

        val mockedService = declareMock<IMyService>(named(("myService")))
        val injectedService = get<IMyService>(named(("myService")))

        assertEquals(mockedService, injectedService)

        val mockedService2 = declareMock<IMyService2>(named(("myService2")))
        val injectedService2 = get<IMyService2>(named(("myService2")))

        assertEquals(mockedService2, injectedService2)

        stopKoin()
    }
}