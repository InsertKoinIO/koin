package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.dsl.module.Module
import org.koin.test.ext.assertContexts
import org.koin.test.ext.assertDefinitions
import org.koin.test.ext.assertRemainingInstances
import org.koin.test.ext.getOrNull

class NamedBeansTest {

    class DataSourceModule : Module() {
        override fun context() =
                applicationContext {
                    provide(name = "debugDatasource") { DebugDatasource() } bind (Datasource::class)
                    provide(name = "ProdDatasource") { ProdDatasource() } bind (Datasource::class)
                }
    }


    interface Datasource
    class DebugDatasource : Datasource
    class ProdDatasource : Datasource

    @Test
    fun `should get named bean`() {
        val ctx = Koin().build(DataSourceModule())

        val debug = ctx.get<Datasource>("debugDatasource")
        val prod = ctx.get<Datasource>("ProdDatasource")

        Assert.assertNotNull(debug)
        Assert.assertNotNull(prod)

        ctx.assertDefinitions(2)
        ctx.assertRemainingInstances(2)
        ctx.assertContexts(1)
    }

    @Test
    fun `should not get named bean`() {
        val ctx = Koin().build(DataSourceModule())

        val other = ctx.getOrNull<Datasource>("otherDatasource")

        Assert.assertNull(other)

        ctx.assertDefinitions(2)
        ctx.assertRemainingInstances(0)
        ctx.assertContexts(1)
    }
}