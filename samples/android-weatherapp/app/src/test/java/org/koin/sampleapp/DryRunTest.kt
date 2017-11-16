package org.koin.sampleapp

import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.sampleapp.di.*
import org.koin.standalone.startContext
import org.koin.test.KoinTest
import org.koin.test.dryRun

/**
 * Created by arnaud on 11/10/2017.
 */
class DryRunTest : KoinTest {

    class DefaultPropertiesModule : Module() {
        override fun context() = applicationContext {
            property(RemoteDataSourceModule.SERVER_URL to "http://test.me")
        }
    }

    @Test
    fun normalConfiguration() {
        startContext(listOf(DefaultPropertiesModule()) + weatherAppModules())
        dryRun()
    }

    @Test
    fun testRemoteConfiguration() {
        startContext(listOf(DefaultPropertiesModule()) + testRemoteDatasource())
        dryRun()
    }

    @Test
    fun testLocalConfiguration() {
        startContext(testLocalDatasource())
        dryRun()
    }
}