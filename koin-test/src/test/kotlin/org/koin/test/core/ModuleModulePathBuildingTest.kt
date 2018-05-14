package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.koin.core.path.ModulePathRegistry
import org.koin.dsl.path.ModulePath

class ModuleModulePathBuildingTest {

    lateinit var pathRegistry: ModulePathRegistry

    @Before
    fun before() {
        pathRegistry = ModulePathRegistry()
    }

    @Test
    fun `should create Scope`() {
        val scope = pathRegistry.makePath("org.koin")

        Assert.assertEquals(3, pathRegistry.paths.size)
        Assert.assertEquals("koin", scope.name)
    }

    @Test
    fun `should create several hierachical Scopes`() {
        pathRegistry.makePath("org.koin")
        val scope = pathRegistry.makePath("org.koin.test")

        Assert.assertEquals(4, pathRegistry.paths.size)
        Assert.assertEquals("test", scope.name)
    }

    @Test
    fun `should get scopes`() {
        pathRegistry.makePath("org.koin.test")

        Assert.assertEquals("org", pathRegistry.getPath("org").name)
        Assert.assertEquals("koin", pathRegistry.getPath("org.koin").name)
        Assert.assertEquals("test", pathRegistry.getPath("org.koin.test").name)
        Assert.assertEquals(ModulePath.ROOT, pathRegistry.getPath("").name)
    }

    @Test
    fun `should get all scopes from`() {
        pathRegistry.makePath("org.koin.test")

        Assert.assertEquals(3, pathRegistry.getAllPathsFrom("org").size)
        Assert.assertEquals(2, pathRegistry.getAllPathsFrom("org.koin").size)
        Assert.assertEquals(1, pathRegistry.getAllPathsFrom("org.koin.test").size)
    }

    @Test
    fun `should not get all scopes from`() {
        pathRegistry.makePath("org.koin")

        try {
            pathRegistry.getAllPathsFrom("or")
            fail()
        } catch (e: Exception) {
        }
        try {
            pathRegistry.getAllPathsFrom("org.koi")
            fail()
        } catch (e: Exception) {
        }
    }

    @Test
    fun `should get all multi scopes`() {
        pathRegistry.makePath("org.koin.test")
        pathRegistry.makePath("org.koin.core")

        Assert.assertEquals(4, pathRegistry.getAllPathsFrom("org").size)
        Assert.assertEquals(3, pathRegistry.getAllPathsFrom("org.koin").size)
        Assert.assertEquals(1, pathRegistry.getAllPathsFrom("org.koin.test").size)
        Assert.assertEquals(1, pathRegistry.getAllPathsFrom("org.koin.core").size)
    }
}