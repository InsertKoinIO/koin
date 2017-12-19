package org.koin.sampleapp

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.Koin
import org.koin.log.PrintLogger
import org.koin.sampleapp.di.Properties.SERVER_URL
import org.koin.sampleapp.di.testLocalDatasource
import org.koin.sampleapp.di.testRemoteDatasource
import org.koin.sampleapp.di.weatherAppModules
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest
import org.koin.test.dryRun

class DryRunTest : KoinTest {

    fun defaultProperties() = mapOf(SERVER_URL to "http://test.me")

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun normalConfiguration() {
        startKoin(weatherAppModules, properties = defaultProperties())
        dryRun()
    }

    @Test
    fun testRemoteConfiguration() {
        startKoin(testRemoteDatasource, properties = defaultProperties())
        dryRun()
    }

    @Test
    fun testLocalConfiguration() {
        startKoin(testLocalDatasource, properties = defaultProperties())
        dryRun()
    }
}