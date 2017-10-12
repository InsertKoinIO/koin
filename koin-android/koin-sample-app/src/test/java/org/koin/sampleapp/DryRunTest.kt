package org.koin.sampleapp

import org.junit.Test
import org.koin.Koin
import org.koin.sampleapp.di.WeatherModule
import org.koin.sampleapp.di.weatherAppModules
import org.koin.sampleapp.di.testLocalDatasource
import org.koin.sampleapp.di.testRemoteDatasource

/**
 * Created by arnaud on 11/10/2017.
 */
class DryRunTest {

    @Test
    fun dryRun() {
        Koin().properties(mapOf(Pair(WeatherModule.SERVER_URL, "http://test.me"))).build(weatherAppModules()).dryRun()
    }

    @Test
    fun testDryRun() {
        Koin().properties(mapOf(Pair(WeatherModule.SERVER_URL, "http://test.me"))).build(testRemoteDatasource()).dryRun()
    }

    @Test
    fun testLocalDataSourceDryRun() {
        Koin().build(testLocalDatasource()).dryRun()
    }
}