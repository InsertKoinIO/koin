package org.koin.sampleapp

import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.sampleapp.di.*
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
    fun dryRun() {
        dryRun(listOf(DefaultPropertiesModule()) + weatherAppModules())
    }

    @Test
    fun testDryRun() {
        dryRun(listOf(DefaultPropertiesModule()) + testRemoteDatasource())
    }

    @Test
    fun testLocalDataSourceDryRun() {
        dryRun(testLocalDatasource())
    }
}