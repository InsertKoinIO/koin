package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class NamedBeansTest : AutoCloseKoinTest() {

    val DataSourceModule = applicationContext {
        provide(name = "debugDatasource") { DebugDatasource() } bind (Datasource::class)
        provide(name = "ProdDatasource") { ProdDatasource() } bind (Datasource::class)
    }

    interface Datasource
    class DebugDatasource : Datasource
    class ProdDatasource : Datasource

    @Test
    fun `should get named bean`() {
        startKoin(listOf(DataSourceModule))

        val debug = get<Datasource>("debugDatasource")
        val prod = get<Datasource>("ProdDatasource")

        Assert.assertNotNull(debug)
        Assert.assertNotNull(prod)

        assertDefinitions(2)
        assertRemainingInstances(2)
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
        assertRemainingInstances(0)
        assertContexts(1)
    }
}