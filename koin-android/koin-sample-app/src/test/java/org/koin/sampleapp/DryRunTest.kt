package org.koin.sampleapp

import org.junit.Test
import org.koin.Koin
import org.koin.sampleapp.di.RxTestModule
import org.koin.sampleapp.di.WeatherModule
import org.koin.sampleapp.di.allModules

/**
 * Created by arnaud on 11/10/2017.
 */
class DryRunTest {

    @Test
    fun dryRun() {
        Koin().properties(mapOf(Pair(WeatherModule.SERVER_URL, "http://test.me"))).build(allModules()).dryRun()
    }

    @Test
    fun testDryRun() {
        Koin().properties(mapOf(Pair(WeatherModule.SERVER_URL, "http://test.me"))).build(RxTestModule(), WeatherModule()).dryRun()
    }
}