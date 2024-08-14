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
    }

    class AndroidComponentA(val androidContext: Context)
    class AndroidComponentB(val androidComponent: AndroidComponentA)
    class AndroidComponentC(val application: Application)
    class OtherService(val url: String)

    @Test
    fun `should verify android module`() {
        sampleModule.verify()

        sampleModule.androidVerify()
    }
}