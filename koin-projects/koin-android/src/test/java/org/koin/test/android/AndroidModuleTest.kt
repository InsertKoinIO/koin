package org.koin.test.android

import android.app.Application
import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.logger.EmptyLogger
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.Mockito.mock

/**
 * Android Module Tests
 */
class AndroidModuleTest : KoinTest {

    companion object {
        const val URL = "URL"
    }

    private val SampleModule = module {
        single { AndroidComponentA(androidContext()) }
        single { AndroidComponentB(get()) }
        single { AndroidComponentC(androidApplication()) }
        single { OtherService(getProperty(URL)) }
    }

    class AndroidComponentA(val androidContext: Context)
    class AndroidComponentB(val androidComponent: AndroidComponentA)
    class AndroidComponentC(val application: Application)
    class OtherService(val url: String)

    @Test
    fun `should inject with android context`() {
        val mockedContext = mock(Context::class.java)

        val koin = koinApplication {
            androidContext(mockedContext)
            modules(SampleModule)
        }.koin

        koin.get<AndroidComponentA>()
    }

    @Test
    fun `should inject with android application`() {
        val mockedContext = mock(Application::class.java)

        val koin = koinApplication {
            androidContext(mockedContext)
            modules(SampleModule)
        }.koin

        koin.get<AndroidComponentC>()
    }

    @Test
    fun `should make DI with serveral components`() {
        val mockedContext = mock(Context::class.java)

        val koin = koinApplication {
            androidContext(mockedContext)
            modules(SampleModule)
        }.koin

        val a = koin.get<AndroidComponentA>()
        val b = koin.get<AndroidComponentB>()

        assertEquals(a, b.androidComponent)
    }

    @Test
    fun `should inject property`() {
        val value = "URL"
        val koin = koinApplication {
            properties(hashMapOf(URL to value))
            modules(SampleModule)
        }.koin

        val s = koin.get<OtherService>()

        assertEquals(value, s.url)
    }

    @Test
    fun `default to empty logger`() {
        val mockedContext = mock(Application::class.java)

        koinApplication {
            androidContext(mockedContext)
            modules(SampleModule)
        }.koin

        assertTrue(KoinApplication.logger is EmptyLogger)
    }
}