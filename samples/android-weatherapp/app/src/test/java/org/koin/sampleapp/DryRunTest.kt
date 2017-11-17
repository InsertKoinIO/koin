package org.koin.sampleapp

import org.junit.Test
import org.koin.sampleapp.di.Properties.SERVER_URL
import org.koin.sampleapp.di.testLocalDatasource
import org.koin.sampleapp.di.testRemoteDatasource
import org.koin.sampleapp.di.weatherAppModules
import org.koin.standalone.startContext
import org.koin.test.KoinTest
import org.koin.test.dryRun

/**
 * Created by arnaud on 11/10/2017.
 */
class DryRunTest : KoinTest {

    fun defaultProperties() = mapOf(SERVER_URL to "http://test.me")

    @Test
    fun normalConfiguration() {
        startContext(weatherAppModules(), properties = defaultProperties())
        dryRun()
    }

    @Test
    fun testRemoteConfiguration() {
        startContext(testRemoteDatasource(), properties = defaultProperties())
        dryRun()
    }

    @Test
    fun testLocalConfiguration() {
        startContext(testLocalDatasource(), properties = defaultProperties())
        dryRun()
    }
}