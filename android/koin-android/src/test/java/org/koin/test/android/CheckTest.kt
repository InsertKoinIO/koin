package org.koin.test.android

import android.content.Context
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.checkKoinModules
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import kotlin.reflect.KClass

class PrefsManager(val context: Context)

// KoinModulesTest.kt
class KoinModulesTest : KoinTest {

    // KoinAppModule.kt
    val prefsModule = module {
        single { PrefsManager(context = get()) } // package.PrefsManager constructor(context: Context)
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { kClass: KClass<*> ->
        // invokes properly with android.content.Context
        Mockito.mock(kClass.java)
    }

    @Test
    fun checkAllModules() {
            checkKoinModules(listOf(prefsModule)){
                withInstance<Context>()
            }
    }
}