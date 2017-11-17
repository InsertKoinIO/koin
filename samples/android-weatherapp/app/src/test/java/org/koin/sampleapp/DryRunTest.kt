package org.koin.sampleapp

import org.junit.Before
import org.junit.Test
import org.koin.Koin
import org.koin.log.PrintLogger
import org.koin.sampleapp.di.Properties.SERVER_URL
import org.koin.sampleapp.di.testLocalDatasource
import org.koin.sampleapp.di.testRemoteDatasource
import org.koin.sampleapp.di.weatherAppModules
import org.koin.standalone.startContext
import org.koin.test.KoinTest
import org.koin.test.dryRun

class DryRunTest : KoinTest {

    fun defaultProperties() = mapOf(SERVER_URL to "http://test.me")

    @Before
    fun before() {
        Koin.logger = PrintLogger()
    }

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