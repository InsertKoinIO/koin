package org.koin.test.module

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.koin.core.path.PathRegistry
import org.koin.dsl.path.Path

class PathBuildingTest {

    lateinit var pathRegistry: PathRegistry

    @Before
    fun before() {
        pathRegistry = PathRegistry()
    }

    private fun createPath(p: String): Path {
        val path = pathRegistry.makePath(p)
        pathRegistry.savePath(path)
        return path
    }

    @Test
    fun `should create path`() {
        val path = createPath("org.koin")

        Assert.assertEquals(3, pathRegistry.paths.size)
        Assert.assertEquals("koin", path.name)
    }


    @Test
    fun `should create several hierachical paths`() {
        createPath("org.koin")
        val path = createPath("org.koin.test")

        Assert.assertEquals(4, pathRegistry.paths.size)
        Assert.assertEquals("test", path.name)
    }

    @Test
    fun `should create several hierachical paths without error`() {
        createPath("org.koin")
        createPath("org.koin.test")
        val path = createPath("org.koin")

        Assert.assertEquals(4, pathRegistry.paths.size)
        Assert.assertEquals("koin", path.name)
    }

    @Test
    fun `should get paths`() {
        createPath("org.koin.test")

        Assert.assertEquals("org", pathRegistry.getPath("org").name)
        Assert.assertEquals("koin", pathRegistry.getPath("org.koin").name)
        Assert.assertEquals("test", pathRegistry.getPath("org.koin.test").name)
        Assert.assertEquals(Path.ROOT, pathRegistry.getPath("").name)
    }

    @Test
    fun `should get all paths from`() {
        createPath("org.koin.test")

        Assert.assertEquals(3, pathRegistry.getAllPathsFrom("org").size)
        Assert.assertEquals(2, pathRegistry.getAllPathsFrom("org.koin").size)
        Assert.assertEquals(1, pathRegistry.getAllPathsFrom("org.koin.test").size)
    }

    @Test
    fun `should not get all paths from`() {
        createPath("org.koin")

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
    fun `should get all multi paths`() {
        createPath("org.koin.test")
        createPath("org.koin.core")

        Assert.assertEquals(4, pathRegistry.getAllPathsFrom("org").size)
        Assert.assertEquals(3, pathRegistry.getAllPathsFrom("org.koin").size)
        Assert.assertEquals(1, pathRegistry.getAllPathsFrom("org.koin.test").size)
        Assert.assertEquals(1, pathRegistry.getAllPathsFrom("org.koin.core").size)
    }
}