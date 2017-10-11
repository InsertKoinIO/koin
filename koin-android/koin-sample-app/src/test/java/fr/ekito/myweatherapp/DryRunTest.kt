package fr.ekito.myweatherapp

import fr.ekito.myweatherapp.di.RxTestModule
import koin.sampleapp.di.WeatherModule
import koin.sampleapp.di.allModules
import org.junit.Test
import org.koin.Koin

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