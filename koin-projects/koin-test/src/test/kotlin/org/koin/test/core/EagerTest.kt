package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.core.KoinContext
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.createEagerInstances
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances
import org.koin.test.ext.koin.beanDefinitions

class EagerTest : AutoCloseKoinTest() {

    val noEager = module {
        single { A() }
        single { B() }
    }

    val onlyA = module {
        single(eager = true) { A() }
        single { B() }
    }

    val two = module {
        single(eager = true) { A() }
        single(eager = true) { B() }
    }

    val module = module(eager = true) {
        single { A() }
        single { B() }
    }

    val submodule1 = module {
        single { A() }

        module("mod1", eager = true) {
            single { B() }
        }
    }

    val submodule2 = module {
        module("mod1", eager = true) {
            single { A() }
            module("mod2") {
                single { B() }
            }
        }
    }


    class A
    class B

    private fun KoinContext() = (StandAloneContext.koinContext as KoinContext)

    @Test
    fun `default no eager`() {
        startKoin(listOf(noEager))
        val definitions = KoinContext().beanDefinitions()
        val a = definitions.first { it.clazz == A::class }
        val b = definitions.first { it.clazz == B::class }

        assertDefinitions(2)
        assertRemainingInstances(0)

        Assert.assertFalse(a.isEager)
        Assert.assertFalse(b.isEager)

        createEagerInstances()
        assertRemainingInstances(0)
    }

    @Test
    fun `one def eager`() {
        startKoin(listOf(onlyA))
        val definitions = KoinContext().beanDefinitions()
        val a = definitions.first { it.clazz == A::class }
        val b = definitions.first { it.clazz == B::class }

        assertDefinitions(2)
        assertRemainingInstances(0)

        Assert.assertTrue(a.isEager)
        Assert.assertFalse(b.isEager)

        createEagerInstances()
        assertRemainingInstances(1)
    }

    @Test
    fun `two def eager`() {
        startKoin(listOf(two))
        val definitions = KoinContext().beanDefinitions()
        val a = definitions.first { it.clazz == A::class }
        val b = definitions.first { it.clazz == B::class }

        assertDefinitions(2)
        assertRemainingInstances(0)

        Assert.assertTrue(a.isEager)
        Assert.assertTrue(b.isEager)

        createEagerInstances()
        assertRemainingInstances(2)
    }

    @Test
    fun `module def eager`() {
        startKoin(listOf(module))
        val definitions = KoinContext().beanDefinitions()
        val a = definitions.first { it.clazz == A::class }
        val b = definitions.first { it.clazz == B::class }

        assertDefinitions(2)
        assertRemainingInstances(0)

        Assert.assertTrue(a.isEager)
        Assert.assertTrue(b.isEager)

        createEagerInstances()
        assertRemainingInstances(2)
    }

    @Test
    fun `submodule eager`() {
        startKoin(listOf(submodule1))
        val definitions = KoinContext().beanDefinitions()
        val a = definitions.first { it.clazz == A::class }
        val b = definitions.first { it.clazz == B::class }

        assertDefinitions(2)
        assertRemainingInstances(0)

        Assert.assertFalse(a.isEager)
        Assert.assertTrue(b.isEager)

        createEagerInstances()
        assertRemainingInstances(1)
    }

    @Test
    fun `submodule2 eager`() {
        startKoin(listOf(submodule2))
        val definitions = KoinContext().beanDefinitions()
        val a = definitions.first { it.clazz == A::class }
        val b = definitions.first { it.clazz == B::class }

        assertDefinitions(2)
        assertRemainingInstances(0)

        Assert.assertTrue(a.isEager)
        Assert.assertFalse(b.isEager)

        createEagerInstances()
        assertRemainingInstances(1)
    }
}