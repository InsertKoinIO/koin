package org.koin.test.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
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
    }

    class AndroidComponentA(val androidContext: Context)
    class AndroidComponentB(val androidComponent: AndroidComponentA)
    interface Service
    class SomeService : Service

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
    fun shouldInjectAdHocDeclaredDepdendency() {
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
}