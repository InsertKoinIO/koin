package org.koin.test.android

import android.app.Application
import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.test.verify.androidVerify
import org.koin.android.test.verify.verify
import org.koin.core.logger.EmptyLogger
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.mockito.Mockito.mock

/**
 * Android Module Tests
 */
class AndroidModuleTest : KoinTest {

    companion object {
        const val URL = "URL"
    }

    private val sampleModule = module {
        single { AndroidComponentA(androidContext()) }
        single { AndroidComponentB(get()) }
        single { AndroidComponentC(androidApplication()) }
        single { OtherService(getProperty(URL)) }
        single { p -> MyOtherService(p.get(),get()) }
    }

    class AndroidComponentA(val androidContext: Context)
    class AndroidComponentB(val androidComponent: AndroidComponentA)
    class AndroidComponentC(val application: Application)
    class OtherService(val url: String)
    class Id
    class MyOtherService(val param : Id, val o: OtherService)

    @Test
    fun `should verify module`() {
        sampleModule.verify(
            injections = injectedParameters(
                definition<MyOtherService>(Id::class)
            )
        )
    }

    @Test
    fun `should verify android module`() {
        sampleModule.androidVerify(
            injections = injectedParameters(
                definition<MyOtherService>(Id::class)
            )
        )
    }
}