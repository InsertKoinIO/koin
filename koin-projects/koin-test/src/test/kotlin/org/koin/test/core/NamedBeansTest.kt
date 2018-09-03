package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders

class NamedBeansTest : AutoCloseKoinTest() {

    val DataSourceModule = module {
        single(name = "debug") { DebugDatasource() } bind (Datasource::class)
        single(name = "prod") { ProdDatasource() } bind (Datasource::class)
    }

    val ServiceModule = module {
        single(name = "debug") { Service(get("debug")) } bind (Service::class)
    }

    interface Datasource
    class DebugDatasource : Datasource
    class ProdDatasource : Datasource

    class Service(val datasource: Datasource)

    @Test
    fun `should get named bean`() {
        startKoin(listOf(DataSourceModule))

        val debug = get<Datasource>("debug")
        val prod = get<Datasource>("prod")

        Assert.assertNotNull(debug)
        Assert.assertNotNull(prod)

        assertDefinitions(2)
        assertRemainingInstanceHolders(2)
        assertContexts(1)
    }

    @Test
    fun `should not get named bean`() {
        startKoin(listOf(DataSourceModule))

        try {
            get<Datasource>("otherDatasource")
            fail()
        } catch (e: Exception) {
        }

        assertDefinitions(2)
        assertRemainingInstanceHolders(0)
        assertContexts(1)
    }

    @Test
    fun `should resolve different types for same bean name`() {
        startKoin(listOf(DataSourceModule, ServiceModule))

        val debug = get<Datasource>("debug")
        val debugService = get<Service>("debug")

        Assert.assertNotNull(debug)
        Assert.assertNotNull(debugService)
        Assert.assertEquals(debug, debugService.datasource)

        assertDefinitions(3)
        assertRemainingInstanceHolders(2)
        assertContexts(1)
    }
}